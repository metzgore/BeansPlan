package de.metzgore.beansplan.settings.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import de.metzgore.beansplan.R;

public class AppSettingsImp implements AppSettings {

    private Context context;

    public AppSettingsImp(final Context context) {
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
    public String getLastOpenedScheduleId() {
        return getStringPreference(R.string.pref_key_last_opened_schedule_id, context.getString(R.string.fragment_daily_schedule_id));
    }

    @Override
    public void setLastOpenedFragmentId(final String id) {
        putStringPreference(R.string.pref_key_last_opened_schedule_id, id);
    }

    private boolean getBooleanPreference(final int keyResourceId, final boolean defaultValue) {
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(context.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }

    private String getStringPreference(final int keyResourceId, final String defaultValue) {
        String value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(context.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }

    private int getIntPreference(final int keyResourceId, final int defaultValue) {
        int value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(context.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }

    private void putIntPreference(final int keyResourceId, final int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            preferences.edit().putInt(context.getResources().getString(keyResourceId), value).apply();
        }
    }

    private void putStringPreference(final int keyResourceId, final String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            preferences.edit().putString(context.getResources().getString(keyResourceId), value).apply();
        }
    }
}
