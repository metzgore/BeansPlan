package de.metzgore.beansplan.shared;

import android.arch.lifecycle.LiveData;

import de.metzgore.beansplan.data.Resource;

public interface IScheduleViewModel<T> {

    LiveData<Resource<T>> getSchedule();

    void loadScheduleFromNetwork();

    void loadSchedule();
}
