package de.metzgore.rbtvschedule.settings.repository;

import android.support.annotation.IdRes;

public interface AppSettings {

    boolean shouldHidePastShows();

    boolean shouldHidePastDays();

    int getDefaultScheduleValue();

    boolean shouldRememberLastOpenedSchedule();

    int getLastOpenedScheduleId();

    void saveLastOpenedScheduleId(@IdRes int id);
}
