import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.util.di.Injector
import okio.Okio

object TestUtils {
    fun createDailySchedule(): DailySchedule {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/daily_schedule_09_05_18.json")
        val source = Okio.buffer(Okio.source(inputStream))

        return Injector.provideGson().fromJson(source.readUtf8(), DailySchedule::class.java)
    }

    fun createWeeklySchedule(): WeeklySchedule {
        val inputStream = javaClass.classLoader
                .getResourceAsStream("api-response/weekly_schedule_one_week.json")
        val source = Okio.buffer(Okio.source(inputStream))

        return Injector.provideGson().fromJson(source.readUtf8(), WeeklySchedule::class.java)
    }
}