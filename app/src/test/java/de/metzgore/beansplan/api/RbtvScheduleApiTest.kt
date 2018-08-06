package de.metzgore.beansplan.api

import android.arch.core.executor.testing.InstantTaskExecutorRule
import de.metzgore.beansplan.LiveDataTestUtil.getValue
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.util.LiveDataCallAdapterFactory
import de.metzgore.beansplan.util.di.Injector
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.apache.commons.lang3.time.DateUtils
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.notNullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@RunWith(JUnit4::class)
class RbtvScheduleApiTest {
    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var service: RbtvScheduleApi

    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
                .baseUrl(mockWebServer.url("/"))
                .addConverterFactory(GsonConverterFactory.create(Injector.provideGson()))
                .addCallAdapterFactory(LiveDataCallAdapterFactory())
                .build()
                .create(RbtvScheduleApi::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun scheduleOfCurrentWeek() {
        enqueueResponse("weekly_schedule_one_week.json")
        val weeklySchedule = (getValue(service.scheduleOfCurrentWeek()) as ApiSuccessResponse).body

        val request = mockWebServer.takeRequest()
        assertThat(request.path, `is`("/api/1.0/schedule/schedule.json"))

        assertThat<WeeklyScheduleResponse>(weeklySchedule, notNullValue())
        assertThat(weeklySchedule.isEmpty, `is`(false))

        assertThat(weeklySchedule.size, `is`(7))

        val calendar = GregorianCalendar()
        calendar.set(2018, Calendar.MAY, 7)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)

        // test dateKeys

        val dateKeys = weeklySchedule.dateKeys

        dateKeys.forEach {
            assertThat(DateUtils.truncatedEquals(it, calendar.time, Calendar.YEAR), `is`(true))
            assertThat(DateUtils.truncatedEquals(it, calendar.time, Calendar.MONTH), `is`(true))
            assertThat(DateUtils.truncatedEquals(it, calendar.time, Calendar.DAY_OF_MONTH), `is`(true))

            calendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH) + 1)
        }
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap()) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        mockWebServer.enqueue(mockResponse.setBody(source.readString(Charsets.UTF_8))
        )
    }
}