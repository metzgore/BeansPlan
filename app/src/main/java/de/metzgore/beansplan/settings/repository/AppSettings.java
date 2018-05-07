package de.metzgore.beansplan.settings.repository;

public interface AppSettings {

    boolean shouldHidePastShows();

    boolean shouldHidePastDays();

    int getDefaultScheduleValue();

    boolean shouldRememberLastOpenedSchedule();

    String getLastOpenedScheduleId();

    void setLastOpenedFragmentId(String id);
}
