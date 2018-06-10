package de.metzgore.beansplan.weeklyschedule

import TestUtils
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class WeeklyScheduleDaoImplTest {

    private lateinit var cache: WeeklyScheduleDaoImpl

    @Before
    fun initCache() {
        cache = WeeklyScheduleDaoImpl(InstrumentationRegistry.getContext(), true)
    }

    @Test
    fun saveAndGet() {
        val weeklySchedule = TestUtils.createWeeklySchedule()

        cache.save(weeklySchedule)

        val savedSchedule = cache.get()

        assertThat(weeklySchedule, `is`(savedSchedule))

        cache.save(null)

        val nullSchedule = cache.get()

        assertThat(nullSchedule, `is`(nullValue()))
    }
}