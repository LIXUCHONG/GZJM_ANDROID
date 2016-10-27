package com.jimei.k3wise_mobile;


import android.Manifest;
import android.annotation.TargetApi;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.RingtonePreference;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.jimei.k3wise_mobile.Component.AppCompatPreferenceActivity;
import com.jimei.k3wise_mobile.Util.CommonHelper;
import com.jimei.k3wise_mobile.Util.PreferencesHelper;
import com.jimei.k3wise_mobile.Util.UpdateHelper;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    static Fragment currentFragment = null;
    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // Trigger the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();

        IntentFilter filter = new IntentFilter();
        filter.addAction("exitApp");    //只有持有相同的action的接受者才能接收此广播
        registerReceiver(new ReceiveBroadCast(), filter);
    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
//                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
//                || DataSyncPreferenceFragment.class.getName().equals(fragmentName)
                || AppUpgradePreferenceFragment.class.getName().equals(fragmentName)
                || HttpConnectPreferenceFragment.class.getName().equals(fragmentName);
    }

    @Override
    public boolean onPreferenceStartFragment(PreferenceFragment caller, Preference pref) {
        currentFragment = caller;
        return super.onPreferenceStartFragment(caller, pref);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            if (currentFragment != null) {
                finishPreferencePanel(currentFragment, 0, getIntent());
                currentFragment = null;
            } else {
                finish();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHeaderClick(Header header, int position) {
        if (header.id == R.id.header_upgrade) {
            PreferencesHelper.Save(this, "app_version_name", CommonHelper.getVersionName());
        }
        super.onHeaderClick(header, position);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HttpConnectPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_http_connect);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("http_connect_ip"));
            bindPreferenceSummaryToValue(findPreference("http_connect_port"));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                currentFragment = null;
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class AppUpgradePreferenceFragment extends PreferenceFragment {
        private final int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 1;
        private int permissionGrantResult;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_upgrade);
            setHasOptionsMenu(true);

            bindPreferenceSummaryToValue(findPreference("app_version_name"));
            bindPreferenceSummaryToValue(findPreference("app_upgrade"));

            permissionGrantResult =PackageManager.PERMISSION_GRANTED;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            requestWriteExternalStoragePermission();
            return super.onCreateView(inflater, container, savedInstanceState);
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                currentFragment = null;
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
            if (preference.getTitleRes() == R.string.pref_Preference_upgrade) {
                if (permissionGrantResult ==PackageManager.PERMISSION_GRANTED) {
                    UpdateHelper updateProcess = new UpdateHelper(getActivity());
                    updateProcess.Upgrade();
                }else {
                    requestWriteExternalStoragePermission();
                }
            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        @TargetApi(Build.VERSION_CODES.M)
        public void requestWriteExternalStoragePermission() {
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请 WRITE_EXTERNAL_STORAGE 权限
                    this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
                }
            }
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case WRITE_EXTERNAL_STORAGE_REQUEST_CODE: {
                    // If request is cancelled, the result arrays are empty.
                    if (grantResults.length > 0
                            && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        permissionGrantResult = grantResults[0];
                    } else {
                        permissionGrantResult = PackageManager.PERMISSION_DENIED;
                    }
                }
            }
        }
    }

    public class ReceiveBroadCast extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            SettingsActivity.this.finish();
        }
    }
}
