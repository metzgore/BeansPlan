package de.metzgore.beansplan


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.ViewInteraction
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.BoundedMatcher
import android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.Toolbar
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaSwipeRefreshInteractions.refresh
import de.metzgore.beansplan.util.di.components.DaggerAppComponent
import de.metzgore.beansplan.util.di.modules.ContextModule
import de.metzgore.beansplan.util.di.modules.ScheduleDaoModule
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
    private lateinit var mockWebServer: MockWebServer

    @Rule
    @JvmField
    var activityTestRule = ActivityTestRule(MainActivity::class.java, true, false)


    @Before
    fun setUp() {
        context = getInstrumentation().targetContext
        preferencesEditor = PreferenceManager.getDefaultSharedPreferences(context).edit()

        mockWebServer = MockWebServer()
        mockWebServer.start(43210)

        val app = InstrumentationRegistry
                .getInstrumentation()
                .targetContext
                .applicationContext as BeansPlanApp

        val mockedComponent = DaggerAppComponent.builder().apply {
            scheduleDaoModule(ScheduleDaoModule(true))
            contextModule(ContextModule(context))
        }.build()

        app.appComponent = mockedComponent
        mockedComponent.inject(app)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun displayDefaultDailySchedule() {
        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        matchToolbarTitle(context.getString(R.string.drawer_item_daily_schedule)).check(matches(isDisplayed()))
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

    @Test
    fun displayLoadingDailySchedule() {
        enqueueResponse("daily_schedule_09_05_18.json", delayed = true)

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_loading_view)
        assertDisplayed(R.id.fragment_base_schedule_loading_view_progress)
        assertDisplayed(R.id.fragment_base_schedule_loading_view_text)

        assertNotDisplayed(R.id.fragment_base_schedule_shows_list)
        assertNotDisplayed(R.id.fragment_base_schedule_empty_view)
    }

    @Test
    fun displayLoadedDailySchedule() {
        enqueueResponse("daily_schedule_09_05_18.json", delayed = false)

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertNotDisplayed(R.id.fragment_base_schedule_loading_view)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_progress)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_text)
        assertNotDisplayed(R.id.fragment_base_schedule_empty_view)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 20)
    }

    @Test
    fun displayLoadingDailyScheduleFailed() {
        enqueueErrorResponse()

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)

        assertNotDisplayed(R.id.fragment_base_schedule_loading_view)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_progress)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_text)
        assertNotDisplayed(R.id.fragment_base_schedule_shows_list)
    }

    @Test
    fun displayReloadDailyScheduleSwipe() {
        enqueueResponse("daily_schedule_09_05_18.json")
        enqueueResponse("daily_schedule_20_05_18.json")

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 20)

        refresh()

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
    }

    private fun prepareDailySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()
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

    private fun enqueueErrorResponse() {
        val mockResponse = MockResponse()

        mockResponse.setResponseCode(404)

        mockWebServer.enqueue(mockResponse)
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
