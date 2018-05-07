package de.metzgore.beansplan.shared;

import java.util.Date;

import de.metzgore.beansplan.data.DailySchedule;

public interface UpdatableScheduleFragment {

    void update(DailySchedule dailySchedule);

    Date getDateKey();
}
