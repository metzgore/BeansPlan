package de.metzgore.beansplan.shared;

import android.content.Context;
import android.support.annotation.Nullable;

import com.vincentbrison.openlibraries.android.dualcache.Builder;
import com.vincentbrison.openlibraries.android.dualcache.DualCache;

import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.util.GsonSerializer;

public class ScheduleCacheImpl implements ScheduleCache {

    private final DualCache<DailySchedule> dailyScheduleCache;

    private final DualCache<WeeklySchedule> weeklyScheduleCache;

    public ScheduleCacheImpl(final Context context) {
        dailyScheduleCache = new Builder<DailySchedule>(DAILY_SCHEDULE_KEY, 1).enableLog()
                .useSerializerInRam(1000000, new GsonSerializer<>(DailySchedule.class))
                .useSerializerInDisk(1000000, true, new GsonSerializer<>(DailySchedule.class),
                        context).build();

        weeklyScheduleCache = new Builder<WeeklySchedule>(WEEKLY_SCHEDULE_KEY, 1).enableLog()
                .useSerializerInRam(1000000, new GsonSerializer<>(WeeklySchedule.class))
                .useSerializerInDisk(1000000, true, new GsonSerializer<>(WeeklySchedule.class),
                        context).build();
    }

    @Override
    public void saveDailySchedule(@Nullable DailySchedule item) {
        dailyScheduleCache.put(DAILY_SCHEDULE_KEY, item);
    }

    @Override
    public void saveWeeklySchedule(@Nullable WeeklySchedule item) {
        weeklyScheduleCache.put(WEEKLY_SCHEDULE_KEY, item);
    }

    @Nullable
    @Override
    public DailySchedule getDailySchedule() {
        return dailyScheduleCache.get(DAILY_SCHEDULE_KEY);
    }

    @Nullable
    @Override
    public WeeklySchedule getWeeklySchedule() {
        return weeklyScheduleCache.get(WEEKLY_SCHEDULE_KEY);
    }
}
