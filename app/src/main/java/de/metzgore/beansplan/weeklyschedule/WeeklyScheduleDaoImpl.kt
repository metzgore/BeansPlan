package de.metzgore.beansplan.weeklyschedule

import android.content.Context

import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.DualCache

import de.metzgore.beansplan.data.WeeklySchedule
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.util.GsonSerializer

class WeeklyScheduleDaoImpl(context: Context, cacheOnly: Boolean = false) : WeeklyScheduleDao {

    private var weeklyScheduleCache: DualCache<WeeklySchedule> = if (cacheOnly) {
        Builder<WeeklySchedule>(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY, 2).enableLog()
                .useSerializerInRam(1000000, GsonSerializer(WeeklySchedule::class.java))
                .noDisk().build()
    } else {
        Builder<WeeklySchedule>(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY, 2).enableLog()
                .useSerializerInRam(1000000, GsonSerializer(WeeklySchedule::class.java))
                .useSerializerInDisk(1000000, true, GsonSerializer(WeeklySchedule::class.java),
                        context).build()
    }

    override fun save(item: WeeklySchedule?) {
        weeklyScheduleCache.put(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY, item)
    }

    override fun get(): WeeklySchedule? {
        return weeklyScheduleCache.get(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY)
    }

}
