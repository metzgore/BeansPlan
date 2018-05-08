package de.metzgore.beansplan.settings.repository;

import android.support.annotation.NonNull;

public interface AppSettings {

    boolean shouldHidePastShows();

    boolean shouldHidePastDays();

    String getDefaultScheduleValue();

    boolean shouldRememberLastOpenedSchedule();

    @NonNull
    String getLastOpenedScheduleId();

    void setLastOpenedFragmentId(String id);

    @NonNull
    String getDailyScheduleFragmentId();

    @NonNull
    String getWeeklyScheduleFragmentId();
}
