package de.metzgore.beansplan.settings.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;

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
    public String getRingtonePreferenceValue() {
        return getStringPreference(R.string.pref_key_notification_tone, Settings.System
                .DEFAULT_NOTIFICATION_URI.toString());
    }

    @Override
    public void setRingtonePreferenceValue(String uri) {
        putStringPreference(R.string.pref_key_notification_tone, uri);
    }

    private boolean getBooleanPreference(final int keyResourceId, final boolean defaultValue) {
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getBoolean(context.getResources().getString(keyResourceId),
                    defaultValue);
        }
        return value;
    }

    private String getStringPreference(final int keyResourceId, final String defaultValue) {
        String value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getString(context.getResources().getString(keyResourceId),
                    defaultValue);
        }
        return value;
    }

    private int getIntPreference(final int keyResourceId, final int defaultValue) {
        int value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            value = preferences.getInt(context.getResources().getString(keyResourceId),
                    defaultValue);
        }
        return value;
    }

    private void putIntPreference(final int keyResourceId, final int value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            preferences.edit().putInt(context.getResources().getString(keyResourceId), value)
                    .apply();
        }
    }

    private void putStringPreference(final int keyResourceId, final String value) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (preferences != null) {
            preferences.edit().putString(context.getResources().getString(keyResourceId), value)
                    .apply();
        }
    }
}
