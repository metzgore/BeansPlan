package de.metzgore.rbtvschedule.dailyschedule;

import de.metzgore.rbtvschedule.data.Schedule;

interface DailyScheduleContract {

    interface View {
        void showSchedule(Schedule schedule);
    }

    interface UserActionListener {
        void loadDailySchedule();
    }
}
