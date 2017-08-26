package de.uni_marburg.mathematik.ds.serval.view.activities;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.LibsBuilder;

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Settings view
 */
public class SettingsActivity extends AppCompatPreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getFragmentManager().beginTransaction().replace(
                android.R.id.content,
                new MainPreferenceFragment()
        ).commit();
    }

    public static class MainPreferenceFragment extends PreferenceFragment {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_main);

            Preference versionNamePreference =
                    findPreference(getString(R.string.key_version_name));
            versionNamePreference.setOnPreferenceClickListener(preference -> {
                // TODO Use seperate Preference
                showLibraries(getActivity());
                return true;
            });

            // Feedback preference click listener
            Preference sendFeedBackPreference =
                    findPreference(getString(R.string.key_send_feedback));
            sendFeedBackPreference.setOnPreferenceClickListener(preference1 -> {
                sendFeedback(getActivity());
                return true;
            });
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private static void bindPreferenceSummaryToValue(Preference preference) {
        preference.setOnPreferenceChangeListener(onPreferenceChangeListener);

        onPreferenceChangeListener.onPreferenceChange(
                preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), "")
        );
    }

    private static Preference.OnPreferenceChangeListener onPreferenceChangeListener =
            (preference, newValue) -> {
                String stringValue = newValue.toString();

                if (preference instanceof ListPreference) {
                    // For list preferences, look up the correct display value in the
                    // preference's entries' list
                    ListPreference listPreference = (ListPreference) preference;
                    int index = listPreference.findIndexOfValue(stringValue);

                    // Set the summary to reflect the new value
                    preference.setSummary(index >= 0 ?
                            listPreference.getEntries()[index] :
                            null);
                } else if (preference instanceof EditTextPreference) {
                    if (preference.getKey().equals("dummy")) {
                        // Update the changed gallery name to summary filed
                        preference.setSummary(stringValue);
                    }
                } else {
                    preference.setSummary(stringValue);
                }
                return true;
            };

    private static void showLibraries(Context context) {
        new LibsBuilder().withActivityStyle(Libs.ActivityStyle.LIGHT_DARK_TOOLBAR).start(context);
    }

    /**
     * Sends feedback via a user choosen email app.
     * <p>
     * Including all necessary informations about the device and app version.
     *
     * @param context Application environment
     */
    private static void sendFeedback(Context context) {
        // Avoids the String 'null' if app version couldn't be gathered
        String body = "";
        try {
            // App version
            body = context
                    .getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0)
                    .versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            body = "\n\n-----------------------------\n" +
                    "Please don't remove this information\n" +
                    "Device OS: Android\n" +
                    "Device OS version: " + Build.VERSION.RELEASE + "\n" +
                    "App Version: " + body + "\n" +
                    "Device Brand: " + Build.BRAND + "\n" +
                    "Device Model: " + Build.MODEL + "\n" +
                    "Device Manufacturer: " + Build.MANUFACTURER;
        }

        Intent mailto = new Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts("mailto", context.getString(R.string.email_adress_feedback), null)
        );
        mailto.putExtra(Intent.EXTRA_SUBJECT, "Query from " + context.getString(R.string.app_name));
        mailto.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(
                mailto,
                context.getString(R.string.choose_email_client)
        ));
    }
}
