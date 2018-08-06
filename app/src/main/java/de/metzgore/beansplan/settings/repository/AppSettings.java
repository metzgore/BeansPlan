package de.metzgore.beansplan.settings.repository;

public interface AppSettings {

    boolean shouldHidePastShows();

    boolean shouldHidePastDays();

    String getRingtonePreferenceValue();

    void setRingtonePreferenceValue(String ringtoneUri);

    boolean shouldVibrateOnNotification();
}
