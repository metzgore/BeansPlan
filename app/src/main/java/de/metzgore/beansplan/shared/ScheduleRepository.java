package de.metzgore.beansplan.shared;

import android.arch.lifecycle.LiveData;

import de.metzgore.beansplan.data.DailySchedule;
import de.metzgore.beansplan.data.Resource;
import de.metzgore.beansplan.data.WeeklySchedule;

public interface ScheduleRepository {

    LiveData<Resource<DailySchedule>> loadScheduleOfToday(boolean forceRefresh);

    LiveData<Resource<WeeklySchedule>> loadWeeklySchedule(boolean forceRefresh);

}
