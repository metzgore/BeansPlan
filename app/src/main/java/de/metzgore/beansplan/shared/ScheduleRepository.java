package de.metzgore.beansplan.shared;

import android.arch.lifecycle.LiveData;

import de.metzgore.beansplan.data.BaseSchedule;
import de.metzgore.beansplan.data.Resource;

public interface ScheduleRepository<T extends BaseSchedule> {

    LiveData<Resource<T>> loadSchedule(boolean forceRefresh);

}
