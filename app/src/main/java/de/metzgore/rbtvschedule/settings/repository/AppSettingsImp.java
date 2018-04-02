package de.metzgore.rbtvschedule.settings.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.metzgore.rbtvschedule.R;

public class AppSettingsImp implements AppSettings {

    private Context context;

    public AppSettingsImp(Context context) {
        this.context = context;
    }

    @Override
    public boolean shouldHidePastShows() {
        return getBooleanPreference(R.string.pref_key_hide_past_shows, false);
    }

    @Override
    public boolean shouldHidePastDays() {
        return getBooleanPreference(R.string.pref_key_hide_past_days, false);
    }

    @Override
    public int getDefaultScheduleValue() {
        return Integer.valueOf(getStringPreference(R.string.pref_key_select_default_schedule, context.getString(R.string.pref_select_default_schedule_default)));
    }

    @Override
    public boolean shouldRememberLastOpenedSchedule() {
        return getBooleanPreference(R.string.pref_key_remember_last_opened_schedule, false);
    }

    @Override
    public int getLastOpenedScheduleId() {
        return getIntPreference(R.string.pref_key_last_opened_schedule_id, 0);
    }

    @Override
    public void saveLastOpenedScheduleId(int id) {
        putIntPreference(R.string.pref_key_last_opened_schedule_id, id);
    }

    private boolean getBooleanPreference(int keyResourceId, boolean defaultValue) {
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(context.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }

    private String getStringPreference(int keyResourceId, String defaultValue) {
        String value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(context.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }

    private int getIntPreference(int keyResourceId, int defaultValue) {
        int value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(context.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }

    private void putIntPreference(int keyResourceId, int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            preferences.edit().putInt(context.getResources().getString(keyResourceId), value).apply();
        }
    }
}
