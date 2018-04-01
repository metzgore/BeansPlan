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
    public boolean getHidePastShowsSetting() {
        return getBooleanPreference(R.string.pref_key_hide_past_shows, false);
    }

    @Override
    public boolean getHidePastDaysSetting() {
        return getBooleanPreference(R.string.pref_key_hide_past_days, false);
    }

    @Override
    public int getDefaultScheduleValue() {
        return Integer.valueOf(getStringPreference(R.string.pref_key_select_default_plan, context.getString(R.string.pref_select_default_plan_default)));
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
}
