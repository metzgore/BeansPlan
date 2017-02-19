package de.metzgore.rbtvschedule.dailyschedule;

import java.util.List;

import de.metzgore.rbtvschedule.data.Show;

class DailySchedulePresenter implements DailyScheduleContract.UserActionListener {

    private static final String TAG = DailySchedulePresenter.class.getSimpleName();

    private DailyScheduleContract.View mView;

    DailySchedulePresenter(DailyScheduleContract.View view) {
        mView = view;
    }

    @Override
    public void loadDailySchedule(List<Show> schedule) {
        mView.showSchedule(schedule);
    }
}
