package de.metzgore.beansplan


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertDisplayed
import com.schibsted.spain.barista.assertion.BaristaVisibilityAssertions.assertNotDisplayed
import com.schibsted.spain.barista.interaction.BaristaClickInteractions.clickOn
import com.schibsted.spain.barista.interaction.BaristaMenuClickInteractions.clickMenu
import com.schibsted.spain.barista.interaction.BaristaSleepInteractions.sleep
import com.schibsted.spain.barista.interaction.BaristaSwipeRefreshInteractions.refresh
import de.metzgore.beansplan.util.DateFormatter
import de.metzgore.beansplan.util.ViewPagerItemCountAssertion.Companion.assertViewPagerViewItemCount
import de.metzgore.beansplan.util.di.components.DaggerAppComponent
import de.metzgore.beansplan.util.di.modules.ContextModule
import de.metzgore.beansplan.util.di.modules.ScheduleDaoModule
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okio.Okio
import org.junit.*
import org.junit.runner.RunWith
import java.util.*
import java.util.concurrent.TimeUnit


@RunWith(AndroidJUnit4::class)
@LargeTest
class MainActivityMockedServerTest {

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

    /**
     * Weekly schedule fragment tests
     */

    @Ignore
    //TODO fix test
    fun displayLoadingWeeklySchedule() {
        enqueueResponse("weekly_schedule_one_week.json", delayed = true)

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.id.fragment_weekly_schedule_loading_view_progress)
        assertDisplayed(R.id.fragment_weekly_schedule_loading_view_text)

        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertNotDisplayed(R.string.error_message_weekly_schedule_loading_failed)
    }

    @Test
    fun displayLoadedWeeklySchedule() {
        enqueueResponse("weekly_schedule_one_week.json", delayed = false)

        val dateStart = getDateFor(2018, Calendar.MAY, 7)
        val dateEnd = getDateFor(2018, Calendar.MAY, 13)

        val subTitle = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStart), DateFormatter.formatDate(context, dateEnd))

        activityTestRule.launchActivity(null)

        assertNotDisplayed(R.id.fragment_weekly_schedule_loading_view)
        assertNotDisplayed(R.id.fragment_weekly_schedule_loading_view_progress)
        assertNotDisplayed(R.id.fragment_weekly_schedule_loading_view_text)
        assertNotDisplayed(R.id.fragment_weekly_schedule_empty_view)

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(subTitle)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
    }

    @Test
    fun displayLoadingWeeklyScheduleFailed() {
        enqueueErrorResponse()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)

        assertNotDisplayed(R.id.fragment_weekly_schedule_loading_view)
        assertNotDisplayed(R.id.fragment_weekly_schedule_loading_view_progress)
        assertNotDisplayed(R.id.fragment_weekly_schedule_loading_view_text)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
    }

    @Test
    fun displayReloadWeeklyScheduleSwipeRefresh() {
        enqueueResponse("weekly_schedule_one_week.json")
        enqueueResponse("weekly_schedule_two_weeks.json")

        val dateStartBefore = getDateFor(2018, Calendar.MAY, 7)
        val dateEndBefore = getDateFor(2018, Calendar.MAY, 13)

        val subTitleBefore = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStartBefore), DateFormatter.formatDate(context, dateEndBefore))

        val dateStartAfter = getDateFor(2018, Calendar.MAY, 14)
        val dateEndAfter = getDateFor(2018, Calendar.MAY, 27)

        val subTitleAfter = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStartAfter), DateFormatter.formatDate(context, dateEndAfter))

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitleBefore)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        refresh()

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 14)
        assertDisplayed(subTitleAfter)
    }

    @Test
    fun displayReloadWeeklyScheduleAfterFailWithSwipeRefresh() {
        enqueueErrorResponse()
        enqueueResponse("weekly_schedule_one_week.json")

        val dateStart = getDateFor(2018, Calendar.MAY, 7)
        val dateEnd = getDateFor(2018, Calendar.MAY, 13)

        val subTitle = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStart), DateFormatter.formatDate(context, dateEnd))

        activityTestRule.launchActivity(null)

        assertNotDisplayed(R.id.activity_main_updated_textview)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)

        refresh()

        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
    }

    @Test
    fun displayReloadWeeklyScheduleMenuRefresh() {
        enqueueResponse("weekly_schedule_one_week.json")
        enqueueResponse("weekly_schedule_two_weeks.json")

        val dateStartBefore = getDateFor(2018, Calendar.MAY, 7)
        val dateEndBefore = getDateFor(2018, Calendar.MAY, 13)

        val subTitleBefore = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStartBefore), DateFormatter.formatDate(context, dateEndBefore))

        val dateStartAfter = getDateFor(2018, Calendar.MAY, 14)
        val dateEndAfter = getDateFor(2018, Calendar.MAY, 27)

        val subTitleAfter = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStartAfter), DateFormatter.formatDate(context, dateEndAfter))

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitleBefore)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertDisplayed(subTitleAfter)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 14)
    }

    @Test
    fun displayReloadWeeklyScheduleAfterFailWithMenuRefresh() {
        enqueueErrorResponse()
        enqueueResponse("weekly_schedule_one_week.json")

        val dateStart = getDateFor(2018, Calendar.MAY, 7)
        val dateEnd = getDateFor(2018, Calendar.MAY, 13)

        val subTitle = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStart), DateFormatter.formatDate(context, dateEnd))

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
    }

    @Test
    fun displayReloadWeeklyScheduleAfterFailWithSnackbar() {
        enqueueErrorResponse()
        enqueueResponse("weekly_schedule_one_week.json")

        val dateStart = getDateFor(2018, Calendar.MAY, 7)
        val dateEnd = getDateFor(2018, Calendar.MAY, 13)

        val subTitle = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStart), DateFormatter.formatDate(context, dateEnd))

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickOn(R.string.action_retry)

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
    }

    @Test
    fun displayFailureSnackbarAfterSuccessfulWeeklyScheduleLoadingWithSwipeRefresh() {
        enqueueResponse("weekly_schedule_one_week.json")
        enqueueErrorResponse()

        val dateStart = getDateFor(2018, Calendar.MAY, 7)
        val dateEnd = getDateFor(2018, Calendar.MAY, 13)

        val subTitle = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStart), DateFormatter.formatDate(context, dateEnd))

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        refresh()

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
    }

    @Test
    fun displayFailureSnackbarAfterSuccessfulWeeklyScheduleLoadingWithMenuRefresh() {
        enqueueResponse("weekly_schedule_one_week.json")
        enqueueErrorResponse()

        val dateStart = getDateFor(2018, Calendar.MAY, 7)
        val dateEnd = getDateFor(2018, Calendar.MAY, 13)

        val subTitle = context.getString(R.string.fragment_weekly_schedule_subtitle, DateFormatter
                .formatDate(context, dateStart), DateFormatter.formatDate(context, dateEnd))

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertDisplayed(subTitle)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulWeeklyScheduleLoadingWithSwipeRefresh() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        refresh()

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulWeeklyScheduleLoadingWithMenuRefresh() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulWeeklyScheduleLoadingWithSnackbar() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickOn(R.string.action_retry)
        sleep(100)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    /**
     * Utils
     */

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

    private fun getDateFor(year: Int, month: Int, day: Int): Date {
        val calendar = GregorianCalendar()
        calendar.set(year, month, day)
        return calendar.time
    }
}
