package de.metzgore.beansplan.weeklyschedule

import android.content.Context

import com.vincentbrison.openlibraries.android.dualcache.Builder
import com.vincentbrison.openlibraries.android.dualcache.DualCache

import de.metzgore.beansplan.data.WeeklyScheduleResponse
import de.metzgore.beansplan.shared.WeeklyScheduleDao
import de.metzgore.beansplan.util.GsonSerializer

class WeeklyScheduleDaoImpl(context: Context, cacheOnly: Boolean = false) : WeeklyScheduleDao {

    private var weeklyScheduleCache: DualCache<WeeklyScheduleResponse> = if (cacheOnly) {
        Builder<WeeklyScheduleResponse>(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY, 2).enableLog()
                .useSerializerInRam(1000000, GsonSerializer(WeeklyScheduleResponse::class.java))
                .noDisk().build()
    } else {
        Builder<WeeklyScheduleResponse>(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY, 2).enableLog()
                .useSerializerInRam(1000000, GsonSerializer(WeeklyScheduleResponse::class.java))
                .useSerializerInDisk(1000000, true, GsonSerializer(WeeklyScheduleResponse::class.java),
                        context).build()
    }

    override fun save(item: WeeklyScheduleResponse?) {
        weeklyScheduleCache.put(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY, item)
    }

    override fun get(): WeeklyScheduleResponse? {
        return weeklyScheduleCache.get(WeeklyScheduleDao.WEEKLY_SCHEDULE_KEY)
    }

}
