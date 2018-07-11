import de.metzgore.beansplan.data.DailyScheduleResponse
import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.util.di.Injector
import okio.Okio

object TestUtils {
    fun createDailySchedule(): DailyScheduleResponse {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/daily_schedule_09_05_18.json")
        val source = Okio.buffer(Okio.source(inputStream))

        return Injector.provideGson().fromJson(source.readUtf8(), DailyScheduleResponse::class.java)
    }

    fun createWeeklyScheduleOneWeek(): WeeklyScheduleResponse {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/weekly_schedule_one_week.json")
        val source = Okio.buffer(Okio.source(inputStream))

        return Injector.provideGson().fromJson(source.readUtf8(), WeeklyScheduleResponse::class.java)
    }

    fun createWeeklyScheduleTwoWeek(): WeeklyScheduleResponse {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/weekly_schedule_two_weeks.json")
        val source = Okio.buffer(Okio.source(inputStream))

        return Injector.provideGson().fromJson(source.readUtf8(), WeeklyScheduleResponse::class.java)
    }
}