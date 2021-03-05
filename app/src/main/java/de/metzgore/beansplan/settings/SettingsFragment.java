package de.metzgore.beansplan.settings;

import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import javax.inject.Inject;

import dagger.android.support.AndroidSupportInjection;
import de.metzgore.beansplan.R;
import de.metzgore.beansplan.settings.repository.AppSettings;

public class SettingsFragment extends PreferenceFragmentCompat {

    private static final int REQUEST_CODE_ALERT_RINGTONE = 0;

    @Inject
    AppSettings settings;

    @Override
    public void onAttach(Context context) {
        AndroidSupportInjection.inject(this);
        super.onAttach(context);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        PreferenceManager.setDefaultValues(getActivity(), R.xml.preference_notification, false);

        addPreferencesFromResource(R.xml.preference_notification);

        Uri ringtoneUri = Uri.parse(settings.getRingtonePreferenceValue());
        setRingtoneSummary(ringtoneUri);
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && preference.getKey().equals(getString(R.string.pref_key_open_notification_settings))) {
            openAppNotificationSettings();
            return true;
        } else if (preference.getKey().equals(getString(R.string
                .pref_key_notification_tone))) {
            openRingtonePicker();
            return true;
        } else {
            return super.onPreferenceTreeClick(preference);
        }
    }

    @RequiresApi(28)
    private void openAppNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).putExtra
                (Settings.EXTRA_APP_PACKAGE, getContext().getPackageName());
        startActivity(intent);
    }

    private void openRingtonePicker() {
        Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_DEFAULT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_SHOW_SILENT, true);
        intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_NOTIFICATION_URI);

        String existingValue = settings.getRingtonePreferenceValue();
        if (existingValue != null) {
            if (existingValue.length() == 0) {
                // Select "Silent"
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
            } else {
                intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Uri.parse
                        (existingValue));
            }
        } else {
            // No ringtone has been selected, set to the default
            intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_NOTIFICATION_URI);
        }

        startActivityForResult(intent, REQUEST_CODE_ALERT_RINGTONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ALERT_RINGTONE && data != null) {
            Uri ringtone = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
            if (ringtone != null) {
                settings.setRingtonePreferenceValue(ringtone.toString());
            } else {
                // "Silent" was selected
                settings.setRingtonePreferenceValue("");
            }
            setRingtoneSummary(ringtone);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setRingtoneSummary(@Nullable final Uri uri) {
        Preference pref = findPreference(getString(R.string.pref_key_notification_tone));

        if (pref != null) {
            if (uri != null && uri.toString().length() > 0) {
                Ringtone ringtone = RingtoneManager.getRingtone(getContext(), uri);
                if (ringtone != null) {
                    pref.setSummary(ringtone.getTitle(getContext()));
                } else {
                    pref.setSummary(getString(R.string.pref_notification_ringtone_summary_silent));
                }
            } else {
                pref.setSummary(getString(R.string.pref_notification_ringtone_summary_silent));
            }
        }
    }
}
