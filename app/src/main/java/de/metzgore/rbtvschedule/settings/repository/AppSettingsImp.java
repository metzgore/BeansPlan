package de.metzgore.rbtvschedule.settings.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import de.metzgore.rbtvschedule.R;

public class AppSettingsImp implements AppSettings {

    private Context mContext;

    public AppSettingsImp(Context context) {
        mContext = context;
    }

    @Override
    public boolean getHidePastShowsSetting() {
        return getBooleanPreference(R.string.pref_key_hide_past_shows, false);
    }

    @Override
    public boolean getHidePastDaysSetting() {
        return getBooleanPreference(R.string.pref_key_hide_past_days, false);
    }

    private boolean getBooleanPreference(int keyResourceId, boolean defaultValue) {
        boolean value = defaultValue;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        if (preferences != null) {
            value = preferences.getBoolean(mContext.getResources().getString(keyResourceId), defaultValue);
        }
        return value;
    }
}
