package de.metzgore.beansplan.dailyschedule

import android.content.Context
import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.DualCache
import de.metzgore.beansplan.data.DailySchedule
import de.metzgore.beansplan.shared.DailyScheduleDao
import de.metzgore.beansplan.testing.OpenForTesting
import de.metzgore.beansplan.util.GsonSerializer

@OpenForTesting
class DailyScheduleDaoImpl(context: Context, cacheOnly: Boolean = false) : DailyScheduleDao {

    private var dailyScheduleCache: DualCache<DailySchedule> = if (cacheOnly) {
        Builder<DailySchedule>(DailyScheduleDao.DAILY_SCHEDULE_KEY, 1).enableLog()
                .useSerializerInRam(1000000, GsonSerializer(DailySchedule::class.java))
                .noDisk().build()
    } else {
        Builder<DailySchedule>(DailyScheduleDao.DAILY_SCHEDULE_KEY, 1).enableLog()
                .useSerializerInRam(1000000, GsonSerializer(DailySchedule::class.java))
                .useSerializerInDisk(1000000, true, GsonSerializer(DailySchedule::class.java),
                        context).build()
    }

    override fun save(item: DailySchedule?) {
        dailyScheduleCache.put(DailyScheduleDao.DAILY_SCHEDULE_KEY, item)
    }

    override fun get(): DailySchedule? {
        return dailyScheduleCache.get(DailyScheduleDao.DAILY_SCHEDULE_KEY)
    }
}
