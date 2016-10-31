package de.metzgore.rbtvschedule.dailyschedule;

import de.metzgore.rbtvschedule.data.Schedule;

interface DailyScheduleContract {

    interface View {
        void showSchedule(Schedule schedule);

        void showRefreshIndicator(boolean isRefreshing);

        void showRetrySnackbar(int messageId);

        void hideSnackbar();
    }

    interface UserActionListener {
        void loadDailySchedule();
    }
}
