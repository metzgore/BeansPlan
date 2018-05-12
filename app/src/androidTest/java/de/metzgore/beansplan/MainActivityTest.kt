package de.metzgore.beansplan


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.Toolbar
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    private lateinit var preferencesEditor: SharedPreferences.Editor
    private lateinit var context: Context
    private lateinit var intent: Intent
    private lateinit var mockWebServer: MockWebServer

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)


    @Before
    fun setUp() {
        intent = Intent()
        context = getInstrumentation().targetContext
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

        mockWebServer = MockWebServer()
        mockWebServer.start(43210)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun displayDefaultDailySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(intent)

        matchToolbarTitle(context.getString(R.string.drawer_item_daily_schedule)).check(matches(isDisplayed()))
    }

    @Test
    fun displayDefaultWeeklySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_weekly_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(intent)

        matchToolbarTitle(context.getString(R.string.drawer_item_weekly_schedule)).check(matches(isDisplayed()))
    }

    @Test
    fun displayLastOpenedSchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), true)
        preferencesEditor.putString(context.getString(R.string.pref_key_last_opened_schedule_id), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(intent)

        matchToolbarTitle(context.getString(R.string.drawer_item_daily_schedule)).check(matches(isDisplayed()))

        activityTestRule.activity.finish()

        preferencesEditor.putString(context.getString(R.string.pref_key_last_opened_schedule_id), context.getString(R.string.fragment_weekly_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(intent)

        matchToolbarTitle(context.getString(R.string.drawer_item_weekly_schedule)).check(matches(isDisplayed()))
    }

    @Test
    fun displayLoadingDailySchedule() {
        enqueueResponse("daily_schedule.json", delayed = true)

        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()

        activityTestRule.launchActivity(intent)

        onView(withId(R.id.fragment_base_schedule_loading_view)).check(matches(isDisplayed()))
        onView(withId(R.id.fragment_base_schedule_loading_view_progress)).check((matches(isDisplayed())))
        onView(withId(R.id.fragment_base_schedule_loading_view_text)).check((matches(isDisplayed())))
    }

    @Test
    fun displayFailedDailySchedule() {
    }

    private fun matchToolbarTitle(title: CharSequence): ViewInteraction {
        return onView(isAssignableFrom(Toolbar::class.java))
                .check(matches(withToolbarTitle(`is`(title))))
    }

    private fun withToolbarTitle(textMatcher: Matcher<CharSequence>): Matcher<Any> {
        return object : BoundedMatcher<Any, Toolbar>(Toolbar::class.java) {
            public override fun matchesSafely(toolbar: Toolbar): Boolean {
                return textMatcher.matches(toolbar.title)
            }

            override fun describeTo(description: Description) {
                description.appendText("with toolbar title: ")
                textMatcher.describeTo(description)
            }
        }
    }

    private fun enqueueResponse(fileName: String, headers: Map<String, String> = emptyMap(), delayed: Boolean = false) {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/$fileName")
        val source = Okio.buffer(Okio.source(inputStream))
        val mockResponse = MockResponse()
        for ((key, value) in headers) {
            mockResponse.addHeader(key, value)
        }
        if (delayed)
            mockResponse.setBodyDelay(1, TimeUnit.SECONDS)

        mockWebServer.enqueue(mockResponse.setBody(source.readString(Charsets.UTF_8)))
    }
}
