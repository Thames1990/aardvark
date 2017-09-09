package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;

import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;

/**
 * Created by thames1990 on 02.09.17.
 */
public class SettingsFragment
        extends PreferenceFragment
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    
    private PrefManager prefManager;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        setupPreferences();
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        prefManager = new PrefManager(context);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Serval.getRefWatcher(getActivity()).watch(this);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        boolean isChecked = (boolean) newValue;
        if (key.equals(getString(R.string.preference_show_changelog_key))) {
            prefManager.setShowChangelog(isChecked);
            return true;
        } else if (key.equals(getString(R.string.preference_use_bottom_sheets_key))) {
            prefManager.setUseBottomSheetDialogs(isChecked);
            return true;
        } else if (key.equals(getString(R.string.preference_confirm_exit_key))) {
            prefManager.setConfirmExit(isChecked);
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.preference_send_feedback_key))) {
            sendFeedback();
            return true;
        } else {
            return false;
        }
    }
    
    private void setupPreferences() {
        findPreference(getString(R.string.preference_show_changelog_key))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_use_bottom_sheets_key))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_confirm_exit_key))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_send_feedback_key))
                .setOnPreferenceClickListener(this);
    }
    
    /**
     * Sends feedback via a user choosen email app.
     * <p>
     * Including all necessary informations about the device and app version.
     */
    private void sendFeedback() {
        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        String body = String.format(
                Locale.getDefault(),
                getString(R.string.intent_extra_send_feeback),
                Build.MANUFACTURER,
                Build.MODEL,
                Build.PRODUCT,
                metrics.widthPixels,
                metrics.heightPixels,
                Build.VERSION.RELEASE,
                getString(R.string.versionName)
        );
        
        Intent mailto = new Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts(
                        getString(R.string.intent_type_mailto),
                        getString(R.string.email_adress_feedback),
                        null
                )
        );
        mailto.putExtra(Intent.EXTRA_SUBJECT, String.format(
                Locale.getDefault(),
                getString(R.string.intent_extra_query_from),
                getString(R.string.app_name)
        ));
        mailto.putExtra(Intent.EXTRA_TEXT, body);
        startActivity(Intent.createChooser(
                mailto,
                getString(R.string.chooser_title_send_feedback)
        ));
    }
}
