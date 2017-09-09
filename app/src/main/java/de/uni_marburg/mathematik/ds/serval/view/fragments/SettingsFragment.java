package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.annotation.Nullable;

import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;

/**
 * Created by thames1990 on 02.09.17.
 */
public class SettingsFragment extends PreferenceFragment {
    
    private Context context;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_main);
        setupPreferences();
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Serval.getRefWatcher(getActivity()).watch(this);
    }
    
    private void setupPreferences() {
        Preference showChangelog = findPreference(getString(R.string.preference_show_changelog_key));
        Preference useBottomSheets = findPreference(getString(R.string.preference_use_bottom_sheets_key));
        Preference confirmExit = findPreference(getString(R.string.preference_confirm_exit_key));
        Preference feedback = findPreference(getString(R.string.preference_send_feedback_key));
        feedback.setOnPreferenceClickListener(preference -> {
            sendFeedback();
            return true;
        });
    }
    
    /**
     * Sends feedback via a user choosen email app.
     * <p>
     * Including all necessary informations about the device and app version.
     */
    private void sendFeedback() {
        // Avoids the String 'null' if app version couldn't be gathered
        String body = "";
        try {
            // App version
            body = context.getPackageManager()
                          .getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            body = String.format(
                    Locale.getDefault(),
                    getString(R.string.intent_extra_send_feeback),
                    Build.VERSION.RELEASE,
                    body,
                    Build.BRAND,
                    Build.MODEL,
                    Build.MANUFACTURER
            );
        }
        
        Intent mailto = new Intent(
                Intent.ACTION_SENDTO,
                Uri.fromParts(
                        getString(R.string.intent_type_mailto),
                        context.getString(R.string.email_adress_feedback),
                        null
                )
        );
        mailto.putExtra(Intent.EXTRA_SUBJECT, "Query from " + context.getString(R.string.app_name));
        mailto.putExtra(Intent.EXTRA_TEXT, body);
        context.startActivity(Intent.createChooser(
                mailto,
                context.getString(R.string.chooser_title_send_feedback)
        ));
    }
}
