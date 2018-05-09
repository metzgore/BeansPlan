package de.metzgore.beansplan.shared;

import android.support.annotation.Nullable;

import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.WeeklySchedule;

public interface ScheduleCache {

    String DAILY_SCHEDULE_KEY = "daily_schedule_key";
    String WEEKLY_SCHEDULE_KEY = "weekly_schedule_key";

    void saveDailySchedule(@Nullable DailySchedule item);

    void saveWeeklySchedule(@Nullable WeeklySchedule item);

    @Nullable
    DailySchedule getDailySchedule();

    @Nullable
    WeeklySchedule getWeeklySchedule();

}
