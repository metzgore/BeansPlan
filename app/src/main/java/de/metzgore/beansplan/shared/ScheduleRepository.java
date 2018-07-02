package de.metzgore.beansplan.shared;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.Date;

import de.metzgore.beansplan.data.Resource;

public interface ScheduleRepository<T> {

    LiveData<Resource<T>> loadSchedule(boolean forceRefresh);

    LiveData<T> loadScheduleFromCache(@NonNull Date date);
}
