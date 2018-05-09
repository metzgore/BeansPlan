package de.metzgore.beansplan.utils;

import android.support.annotation.Nullable;

import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.WeeklySchedule;
import de.metzgore.beansplan.shared.ScheduleCache;

public class MockCache implements ScheduleCache {

    private DailySchedule dailySchedule;
    private WeeklySchedule weeklySchedule;

    @Override
    public void saveDailySchedule(@Nullable DailySchedule item) {
        dailySchedule = item;
    }

    @Override
    public void saveWeeklySchedule(@Nullable WeeklySchedule item) {
        weeklySchedule = item;
    }

    @Nullable
    @Override
    public DailySchedule getDailySchedule() {
        return dailySchedule;
    }

    @Nullable
    @Override
    public WeeklySchedule getWeeklySchedule() {
        return weeklySchedule;
    }
}
