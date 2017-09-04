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

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Created by thames1990 on 02.09.17.
 */
public class MainPreferenceFragment extends PreferenceFragment {
    
    private Context context;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_main);
        setupPreferences();
    }
    
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }
    
    private void setupPreferences() {
        Preference feedback = findPreference(getString(R.string.preference_key_send_feedback));
        feedback.setOnPreferenceClickListener(preference -> {
            sendFeedback();
            return true;
        });
    }
    
    /**
     * Sends feedback via a user choosen email app.
     * <p>
     * Including all necessary informations about the device and app version.
     *
     * @param context Application environment
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
                context.getString(R.string.preference_choose_email_client)
        ));
    }
}
