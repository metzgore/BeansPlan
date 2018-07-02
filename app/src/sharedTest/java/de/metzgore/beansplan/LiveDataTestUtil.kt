package de.metzgore.beansplan

import TestUtils
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.Resource
import de.metzgore.beansplan.data.Status
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

object LiveDataTestUtil {
    fun <T> getValue(liveData: LiveData<T>): T {
        val data = arrayOfNulls<Any>(1)
        val latch = CountDownLatch(1)
        val observer = object : Observer<T> {
            override fun onChanged(o: T?) {
                data[0] = o
                latch.countDown()
                liveData.removeObserver(this)
            }
        }
        liveData.observeForever(observer)
        latch.await(2, TimeUnit.SECONDS)

        @Suppress("UNCHECKED_CAST")
        return data[0] as T
    }

    fun createFilledDailyScheduleLiveData(status: Status, forceRefresh: Boolean): LiveData<Resource<DailySchedule>> {
        return createDailyScheduleLiveData(TestUtils.createDailySchedule(), status, forceRefresh)
    }

    fun createEmptyDailyScheduleLiveData(status: Status, forceRefresh: Boolean): LiveData<Resource<DailySchedule>> {
        return createDailyScheduleLiveData(DailySchedule(), status, forceRefresh)
    }

    private fun createDailyScheduleLiveData(schedule: DailySchedule, status: Status, forceRefresh: Boolean): LiveData<Resource<DailySchedule>> {
        val liveData = MutableLiveData<Resource<DailySchedule>>()

        liveData.value = when (status) {
            Status.SUCCESS -> Resource.success(schedule, forceRefresh)
            Status.ERROR -> Resource.error("foo", schedule, forceRefresh)
            Status.LOADING -> Resource.loading(schedule, forceRefresh)
        }

        return liveData
    }

    fun createFilledWeeklyScheduleLiveData(status: Status, forceRefresh: Boolean): LiveData<Resource<WeeklyScheduleResponse>> {
        return createWeeklyScheduleLiveData(TestUtils.createWeeklySchedule(), status, forceRefresh)
    }

    fun createEmptyWeeklyScheduleLiveData(status: Status, forceRefresh: Boolean): LiveData<Resource<WeeklyScheduleResponse>> {
        return createWeeklyScheduleLiveData(WeeklyScheduleResponse(), status, forceRefresh)
    }

    private fun createWeeklyScheduleLiveData(schedule: WeeklyScheduleResponse, status: Status, forceRefresh: Boolean): LiveData<Resource<WeeklyScheduleResponse>> {
        val liveData = MutableLiveData<Resource<WeeklyScheduleResponse>>()

        liveData.value = when (status) {
            Status.SUCCESS -> Resource.success(schedule, forceRefresh)
            Status.ERROR -> Resource.error("foo", schedule, forceRefresh)
            Status.LOADING -> Resource.loading(schedule, forceRefresh)
        }

        return liveData
    }
}