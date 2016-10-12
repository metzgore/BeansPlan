package de.metzgore.rbtvschedule.dailyschedule;

import de.metzgore.rbtvschedule.data.Schedule;

interface DailyScheduleContract {

    interface View {
        void showSchedule(Schedule schedule);

        void showRefreshIndicator(boolean isRefreshing);

        void showRetrySnackbar(int messageId);
    }

    interface UserActionListener {
        void loadDailySchedule();
    }
}
