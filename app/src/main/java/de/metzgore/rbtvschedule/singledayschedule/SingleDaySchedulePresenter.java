package de.metzgore.rbtvschedule.singledayschedule;

import java.util.List;

import de.metzgore.rbtvschedule.data.Show;

class SingleDaySchedulePresenter implements SingleDayScheduleContract.UserActionListener {

    private static final String TAG = SingleDaySchedulePresenter.class.getSimpleName();

    private SingleDayScheduleContract.View mView;

    SingleDaySchedulePresenter(SingleDayScheduleContract.View view) {
        mView = view;
    }

    @Override
    public void loadDailySchedule(List<Show> schedule) {
        mView.showSchedule(schedule);
    }
}
