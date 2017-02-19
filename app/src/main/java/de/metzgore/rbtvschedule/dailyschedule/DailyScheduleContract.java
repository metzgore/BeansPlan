package de.metzgore.rbtvschedule.dailyschedule;

import java.util.List;

import de.metzgore.rbtvschedule.data.Show;

interface DailyScheduleContract {

    interface View {
        void showSchedule(List<Show> schedule);
    }

    interface UserActionListener {
        void loadDailySchedule(List<Show> schedule);
    }
}
