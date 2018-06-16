package de.metzgore.beansplan

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.rule.ActivityTestRule
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
    fun displayDefaultDailySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.string.drawer_item_daily_schedule)
    }

    @Test
    fun displayDefaultWeeklySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_weekly_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.string.drawer_item_weekly_schedule)
    }

    @Test
    fun displayLastOpenedSchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), true)
        preferencesEditor.putString(context.getString(R.string.pref_key_last_opened_schedule_id), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.string.drawer_item_daily_schedule)

        activityTestRule.activity.finish()

        preferencesEditor.putString(context.getString(R.string.pref_key_last_opened_schedule_id), context.getString(R.string.fragment_weekly_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.string.drawer_item_weekly_schedule)
    }
}