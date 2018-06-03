package de.metzgore.beansplan.dailyschedule

import TestUtils
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.nullValue
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DailyScheduleDaoImplTest {

    private lateinit var cache: DailyScheduleDaoImpl

    @Before
    fun initCache() {
        cache = DailyScheduleDaoImpl(InstrumentationRegistry.getContext(), true)
    }

    @Test
    fun saveAndGet() {
        val dailySchedule = TestUtils.createDailySchedule()

        cache.save(dailySchedule)

        val savedSchedule = cache.get()

        assertThat(dailySchedule, `is`(savedSchedule))

        cache.save(null)

        val nullSchedule = cache.get()

        assertThat(nullSchedule, `is`(nullValue()))
    }
}