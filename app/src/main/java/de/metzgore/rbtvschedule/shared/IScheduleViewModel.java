package de.metzgore.rbtvschedule.shared;

import android.arch.lifecycle.LiveData;

import de.metzgore.rbtvschedule.data.Resource;

public interface IScheduleViewModel<T> {

    LiveData<Resource<T>> getSchedule();

    void loadSchedule(boolean forceFromNetwork);
}
