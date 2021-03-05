package de.metzgore.beansplan

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.rule.ActivityTestRule
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class MainActivityTest {

    private lateinit var preferencesEditor: SharedPreferences.Editor
    private lateinit var context: Context

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)


    @Before
    fun setUp() {
        context = getInstrumentation().targetContext
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()
    }

    @Test
    fun displayDefaultWeeklySchedule() {
        activityTestRule.launchActivity(null)

        assertDisplayed(R.string.drawer_item_weekly_schedule)
    }
}