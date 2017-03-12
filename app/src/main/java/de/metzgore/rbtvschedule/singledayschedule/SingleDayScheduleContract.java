package de.metzgore.rbtvschedule.singledayschedule;

import java.util.List;

import de.metzgore.rbtvschedule.data.Show;

interface SingleDayScheduleContract {

    interface View {
        void showSchedule(List<Show> schedule);
    }

    interface UserActionListener {
        void loadDailySchedule(List<Show> schedule);
    }
}
