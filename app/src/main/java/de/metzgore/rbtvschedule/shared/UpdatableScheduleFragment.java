package de.metzgore.rbtvschedule.shared;

import java.util.Date;

import de.metzgore.rbtvschedule.data.DailySchedule;

public interface UpdatableScheduleFragment {

    void update(DailySchedule dailySchedule);

    Date getDateKey();
}
