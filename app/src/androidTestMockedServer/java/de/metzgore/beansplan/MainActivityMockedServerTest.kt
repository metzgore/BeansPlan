package de.metzgore.beansplan


import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.test.InstrumentationRegistry
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.filters.LargeTest
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.schibsted.spain.barista.assertion.BaristaRecyclerViewAssertions.assertRecyclerViewItemCount
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
     * Daily schedule fragment tests
     */

    @Ignore
    //TODO fix test
    fun displayLoadingDailySchedule() {
        enqueueResponse("daily_schedule_09_05_18.json", delayed = true)

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_loading_view)
        assertDisplayed(R.id.fragment_base_schedule_loading_view_progress)
        assertDisplayed(R.id.fragment_base_schedule_loading_view_text)

        assertNotDisplayed(R.id.fragment_base_schedule_shows_list)
        assertNotDisplayed(R.id.fragment_base_schedule_empty_view)
        assertNotDisplayed(R.string.error_message_daily_schedule_loading_failed)
    }

    @Test
    fun displayLoadedDailySchedule() {
        enqueueResponse("daily_schedule_09_05_18.json", delayed = false)

        val date = getDateFor(2018, Calendar.MAY, 9)

        val subTitle = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, date))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertNotDisplayed(R.id.fragment_base_schedule_loading_view)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_progress)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_text)
        assertNotDisplayed(R.id.fragment_base_schedule_empty_view)

        assertDisplayed(R.id.activity_main_updated_textview)
        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 20)
    }

    @Test
    fun displayLoadingDailyScheduleFailed() {
        enqueueErrorResponse()

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)

        assertNotDisplayed(R.id.activity_main_updated_textview)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_progress)
        assertNotDisplayed(R.id.fragment_base_schedule_loading_view_text)
        assertNotDisplayed(R.id.fragment_base_schedule_shows_list)
    }

    @Test
    fun displayReloadDailyScheduleSwipeRefresh() {
        enqueueResponse("daily_schedule_09_05_18.json")
        enqueueResponse("daily_schedule_20_05_18.json")

        val dateBefore = getDateFor(2018, Calendar.MAY, 9)
        val subTitleBefore = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, dateBefore))

        val dateAfter = getDateFor(2018, Calendar.MAY, 20)
        val subTitleAfter = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, dateAfter))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitleBefore)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 20)

        refresh()

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitleAfter)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
    }

    @Test
    fun displayReloadDailyScheduleAfterFailWithSwipeRefresh() {
        enqueueErrorResponse()
        enqueueResponse("daily_schedule_20_05_18.json")

        val date = getDateFor(2018, Calendar.MAY, 20)
        val subTitle = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, date))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertNotDisplayed(R.id.activity_main_updated_textview)
        //TODO crashed, Barista bug?
        //assertNotDisplayed(subTitle)

        refresh()

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
    }

    @Test
    fun displayReloadDailyScheduleMenuRefresh() {
        enqueueResponse("daily_schedule_09_05_18.json")
        enqueueResponse("daily_schedule_20_05_18.json")

        val dateBefore = getDateFor(2018, Calendar.MAY, 9)
        val subTitleBefore = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, dateBefore))

        val dateAfter = getDateFor(2018, Calendar.MAY, 20)
        val subTitleAfter = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, dateAfter))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitleBefore)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 20)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitleAfter)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
    }

    @Test
    fun displayReloadDailyScheduleAfterFailWithMenuRefresh() {
        enqueueErrorResponse()
        enqueueResponse("daily_schedule_20_05_18.json")

        val date = getDateFor(2018, Calendar.MAY, 20)
        val subTitle = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, date))


        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
    }

    @Test
    fun displayReloadDailyScheduleAfterFailWithSnackbar() {
        enqueueErrorResponse()
        enqueueResponse("daily_schedule_20_05_18.json")

        val date = getDateFor(2018, Calendar.MAY, 20)
        val subTitle = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, date))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickOn(R.string.action_retry)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
    }

    @Test
    fun displayFailureSnackbarAfterSuccessfulDailyScheduleLoadingWithSwipeRefresh() {
        enqueueResponse("daily_schedule_20_05_18.json")
        enqueueErrorResponse()

        val date = getDateFor(2018, Calendar.MAY, 20)
        val subTitle = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, date))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)

        refresh()

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
    }

    @Test
    fun displayFailureSnackbarAfterSuccessfulDailyScheduleLoadingWithMenuRefresh() {
        enqueueResponse("daily_schedule_20_05_18.json")
        enqueueErrorResponse()

        val date = getDateFor(2018, Calendar.MAY, 20)
        val subTitle = context.getString(R.string.fragment_daily_schedule_subtitle, DateFormatter
                .formatDate(context, date))

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_base_schedule_shows_list)
        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertRecyclerViewItemCount(R.id.fragment_base_schedule_shows_list, 18)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulDailyScheduleLoadingWithSwipeRefresh() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulDailyScheduleLoadingWithMenuRefresh() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickMenu(R.id.action_refresh)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulDailyScheduleLoadingWithSnackbar() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        prepareDailySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickOn(R.string.action_retry)

        assertDisplayed(R.id.fragment_base_schedule_empty_view)
        assertDisplayed(R.string.error_message_daily_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    /**
     * Weekly schedule fragment tests
     */

    @Ignore
    //TODO fix test
    fun displayLoadingWeeklySchedule() {
        enqueueResponse("weekly_schedule_one_week.json", delayed = true)

        prepareWeeklySchedule()

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

        prepareWeeklySchedule()

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

        prepareWeeklySchedule()

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitleBefore)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        refresh()
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertNotDisplayed(R.id.activity_main_updated_textview)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)

        refresh()
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitleBefore)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        clickMenu(R.id.action_refresh)
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.activity_main_updated_textview)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)

        clickMenu(R.id.action_refresh)
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickOn(R.string.action_retry)
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        refresh()
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(subTitle)
        assertDisplayed(R.id.activity_main_updated_textview)
        assertViewPagerViewItemCount(R.id.fragment_weekly_schedule_view_pager, 7)
        assertDisplayed(R.id.fragment_weekly_schedule_view_pager)

        clickMenu(R.id.action_refresh)
        sleep(100)

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

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        refresh()
        sleep(100)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulWeeklyScheduleLoadingWithMenuRefresh() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        prepareWeeklySchedule()

        activityTestRule.launchActivity(null)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)

        clickMenu(R.id.action_refresh)
        sleep(100)

        assertDisplayed(R.id.fragment_weekly_schedule_empty_view)
        assertDisplayed(R.string.error_message_weekly_schedule_loading_failed)
        assertNotDisplayed(R.id.fragment_weekly_schedule_view_pager)
        assertNotDisplayed(R.id.activity_main_updated_textview)
    }

    @Test
    fun displayFailureSnackbarAfterUnsuccessfulWeeklyScheduleLoadingWithSnackbar() {
        enqueueErrorResponse()
        enqueueErrorResponse()

        prepareWeeklySchedule()

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

    private fun prepareDailySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_daily_schedule_id))
        preferencesEditor.commit()
    }

    private fun prepareWeeklySchedule() {
        preferencesEditor.putBoolean(context.getString(R.string.pref_key_remember_last_opened_schedule), false)
        preferencesEditor.putString(context.getString(R.string.pref_key_select_default_schedule), context.getString(R.string.fragment_weekly_schedule_id))
        preferencesEditor.commit()
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

    private fun getDateFor(year: Int, month: Int, day: Int): Date {
        val calendar = GregorianCalendar()
        calendar.set(year, month, day)
        return calendar.time
    }
}
