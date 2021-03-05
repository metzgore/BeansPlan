package de.metzgore.beansplan.shared

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import de.metzgore.beansplan.api.ApiResponse
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.mock
import de.metzgore.beansplan.util.ApiUtil
import de.metzgore.beansplan.util.NetworkBoundResource
import de.metzgore.beansplan.utils.CountingAppExecutors
import de.metzgore.beansplan.utils.InstantAppExecutors
import okhttp3.MediaType
import okhttp3.ResponseBody
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import org.mockito.Mockito.*
import retrofit2.Response
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

@RunWith(Parameterized::class)
class NetworkBoundResourceTest(private val useRealExecutors: Boolean, private val forceRefresh:
Boolean) {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var handleSaveCallResult: (Foo) -> Unit

    private lateinit var handleShouldMatch: (Foo?) -> Boolean

    private lateinit var handleShouldSaveCallResult: (Foo?) -> Boolean

    private lateinit var handleCreateCall: () -> LiveData<ApiResponse<Foo>>

    private val dbData = MutableLiveData<Foo>()

    private lateinit var networkBoundResource: NetworkBoundResource<Foo, Foo>

    private val fetchedOnce = AtomicBoolean(false)
    private lateinit var countingAppExecutors: CountingAppExecutors

    init {
        if (useRealExecutors) {
            countingAppExecutors = CountingAppExecutors()
        }
    }

    @Before
    fun init() {
        val appExecutors = if (useRealExecutors)
            countingAppExecutors.appExecutors
        else
            InstantAppExecutors()
        networkBoundResource = object : NetworkBoundResource<Foo, Foo>(appExecutors, forceRefresh) {
            override fun shouldSave(item: Foo): Boolean {
                return handleShouldSaveCallResult(item)
            }

            override fun saveCallResult(item: Foo) {
                handleSaveCallResult(item)
            }

            override fun shouldFetch(data: Foo?): Boolean {
                // since test methods don't handle repetitive fetching, call it only once
                return handleShouldMatch(data) && fetchedOnce.compareAndSet(false, true)
            }

            override fun loadFromDb(): LiveData<Foo> {
                return dbData
            }

            override fun createCall(): LiveData<ApiResponse<Foo>> {
                return handleCreateCall()
            }
        }
    }

    private fun drain() {
        if (!useRealExecutors) {
            return
        }
        try {
            countingAppExecutors.drainTasks(1, TimeUnit.SECONDS)
        } catch (t: Throwable) {
            throw AssertionError(t)
        }

    }

    @Test
    fun basicFromNetwork() {
        val saved = AtomicReference<Foo>()
        handleShouldMatch = { it == null }
        handleShouldSaveCallResult = { true }
        val fetchedDbValue = Foo(1)
        handleSaveCallResult = { foo ->
            saved.set(foo)
            dbData.setValue(fetchedDbValue)
        }
        val networkResult = Foo(1)
        handleCreateCall = { ApiUtil.createCall(Response.success(networkResult)) }

        val observer = mock<Observer<Resource<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        verify(observer).onChanged(Resource.loading(null, forceRefresh))
        reset(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(networkResult))
        verify(observer).onChanged(Resource.success(fetchedDbValue, forceRefresh))
    }

    @Test
    fun failureFromNetwork() {
        val saved = AtomicBoolean(false)
        handleShouldMatch = { it == null }
        handleShouldSaveCallResult = { true }
        handleSaveCallResult = {
            saved.set(true)
        }
        val body = ResponseBody.create(MediaType.parse("text/html"), "error")
        handleCreateCall = { ApiUtil.createCall(Response.error<Foo>(500, body)) }

        val observer = mock<Observer<Resource<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        verify(observer).onChanged(Resource.loading(null, forceRefresh))
        reset(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(false))
        verify(observer).onChanged(Resource.error("error", null, forceRefresh))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithoutNetwork() {
        val saved = AtomicBoolean(false)
        handleShouldMatch = { it == null }
        handleShouldSaveCallResult = { true }
        handleSaveCallResult = {
            saved.set(true)
        }

        val observer = mock<Observer<Resource<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        verify(observer).onChanged(Resource.loading(null, forceRefresh))
        reset(observer)
        val dbFoo = Foo(1)
        dbData.value = dbFoo
        drain()
        verify(observer).onChanged(Resource.success(dbFoo, forceRefresh))
        assertThat(saved.get(), `is`(false))
        val dbFoo2 = Foo(2)
        dbData.value = dbFoo2
        drain()
        verify(observer).onChanged(Resource.success(dbFoo2, forceRefresh))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithFetchFailure() {
        val dbValue = Foo(1)
        val saved = AtomicBoolean(false)
        handleShouldMatch = { foo -> foo === dbValue }
        handleShouldSaveCallResult = { true }
        handleSaveCallResult = {
            saved.set(true)
        }
        val body = ResponseBody.create(MediaType.parse("text/html"), "error")
        val apiResponseLiveData = MutableLiveData<ApiResponse<Foo>>()
        handleCreateCall = { apiResponseLiveData }

        val observer = mock<Observer<Resource<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        verify(observer).onChanged(Resource.loading(null, forceRefresh))
        reset(observer)

        dbData.value = dbValue
        drain()
        verify(observer).onChanged(Resource.loading(dbValue, forceRefresh))

        apiResponseLiveData.value = ApiResponse.create(Response.error<Foo>(400, body))
        drain()
        assertThat(saved.get(), `is`(false))
        verify(observer).onChanged(Resource.error("error", dbValue, forceRefresh))

        val dbValue2 = Foo(2)
        dbData.value = dbValue2
        drain()
        verify(observer).onChanged(Resource.error("error", dbValue2, forceRefresh))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun dbSuccessWithReFetchSuccess() {
        val dbValue = Foo(1)
        val dbValue2 = Foo(2)
        val saved = AtomicReference<Foo>()
        handleShouldMatch = { foo -> foo === dbValue }
        handleShouldSaveCallResult = { true }
        handleSaveCallResult = { foo ->
            saved.set(foo)
            dbData.setValue(dbValue2)
        }
        val apiResponseLiveData = MutableLiveData<ApiResponse<Foo>>()
        handleCreateCall = { apiResponseLiveData }

        val observer = mock<Observer<Resource<Foo>>>()
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        verify(observer).onChanged(Resource.loading(null, forceRefresh))
        reset(observer)

        dbData.value = dbValue
        drain()
        val networkResult = Foo(1)
        verify(observer).onChanged(Resource.loading(dbValue, forceRefresh))
        apiResponseLiveData.value = ApiResponse.create(Response.success(networkResult))
        drain()
        assertThat(saved.get(), `is`(networkResult))
        verify(observer).onChanged(Resource.success(dbValue2, forceRefresh))
        verifyNoMoreInteractions(observer)
    }

    @Test
    fun doSaveWhenCachedIsNull() {
        val saved = AtomicReference<Boolean>()
        saved.set(false)
        handleShouldMatch = {
            true
        }

        handleSaveCallResult = { _ ->
            saved.set(true)
        }

        val networkResult = Foo(1)
        handleCreateCall = { ApiUtil.createCall(Response.success(networkResult)) }

        val observer = mock<Observer<Resource<Foo>>>()

        handleShouldSaveCallResult = {
            null != it
        }
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        reset(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(true))
    }

    @Test
    fun doNotSaveWhenCachedAndNetworkEqual() {
        val saved = AtomicReference<Boolean>()
        saved.set(false)
        handleShouldMatch = {
            true
        }

        handleSaveCallResult = { _ ->
            saved.set(true)
        }

        val networkResult = Foo(1)
        handleCreateCall = { ApiUtil.createCall(Response.success(networkResult)) }

        val observer = mock<Observer<Resource<Foo>>>()

        handleShouldSaveCallResult = {
            networkResult != it
        }
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        reset(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(false))
    }

    @Test
    fun doSaveWhenCachedAndNetworkNotEqual() {
        val saved = AtomicReference<Boolean>()
        saved.set(false)
        handleShouldMatch = {
            true
        }

        handleSaveCallResult = { _ ->
            saved.set(true)
        }

        val networkResult = Foo(1)
        handleCreateCall = { ApiUtil.createCall(Response.success(networkResult)) }

        val observer = mock<Observer<Resource<Foo>>>()

        //cached data is null
        handleShouldSaveCallResult = {
            Foo(2) != it
        }
        networkBoundResource.asLiveData().observeForever(observer)
        drain()
        reset(observer)
        dbData.value = null
        drain()
        assertThat(saved.get(), `is`(true))
    }

    private data class Foo(var value: Int)

    companion object {
        @Parameterized.Parameters
        @JvmStatic
        fun param(): List<Array<Boolean>> {
            return arrayListOf(arrayOf(true, true), arrayOf(true, false), arrayOf(false, true),
                    arrayOf(false, false))
        }
    }
}