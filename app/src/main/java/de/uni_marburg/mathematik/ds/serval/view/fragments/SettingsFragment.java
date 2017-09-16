package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.text.format.Formatter;
import android.util.DisplayMetrics;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.BuildConfig;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Aardvark;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;

/**
 * Created by thames1990 on 02.09.17.
 */
public class SettingsFragment
        extends PreferenceFragmentCompat
        implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener {
    
    private PrefManager prefManager;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = Aardvark.getPreferences(getContext());
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Aardvark.getRefWatcher(getContext()).watch(this);
    }
    
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        if (BuildConfig.DEBUG) {
            addPreferencesFromResource(R.xml.pref_debug);
            findPreference(getString(R.string.preference_enable_wifi_adb))
                    .setOnPreferenceClickListener(this);
        }
        
        addPreferencesFromResource(R.xml.pref_main);
        findPreference(getString(R.string.preference_show_changelog))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_use_bottom_sheets))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_confirm_exit))
                .setOnPreferenceChangeListener(this);
        findPreference(getString(R.string.preference_send_feedback))
                .setOnPreferenceClickListener(this);
        findPreference(getString(R.string.preference_version)).setSummary(BuildConfig.VERSION_NAME);
    }
    
    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        boolean isChecked = (boolean) newValue;
        if (key.equals(getString(R.string.preference_show_changelog))) {
            prefManager.setShowChangelog(isChecked);
            return true;
        } else if (key.equals(getString(R.string.preference_use_bottom_sheets))) {
            prefManager.setUseBottomSheetDialogs(isChecked);
            return true;
        } else if (key.equals(getString(R.string.preference_confirm_exit))) {
            prefManager.setConfirmExit(isChecked);
            return true;
        } else if (key.equals(getString(R.string.preference_enable_wifi_adb))) {
            enableWifiAdb();
            return true;
        } else {
            return false;
        }
    }
    
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(getString(R.string.preference_send_feedback))) {
            sendFeedback();
            return true;
        } else {
            return false;
        }
    }
    
    private void enableWifiAdb() {
        try {
            Process root = Runtime.getRuntime().exec("su");
            DataOutputStream dos = new DataOutputStream(root.getOutputStream());
            dos.writeBytes("setprop service.adb.tcp.port 5555\n");
            dos.writeBytes("stop adbd\n");
            dos.writeBytes("start adbd\n");
            dos.writeBytes("exit\n");
            dos.flush();
            dos.close();
            root.waitFor();
            
            WifiManager mWifiManager =
                    (WifiManager) getContext().getApplicationContext()
                                              .getSystemService(Context.WIFI_SERVICE);
            int ip = mWifiManager.getConnectionInfo().getIpAddress();
            //noinspection deprecation
            shareWifiADBInfo(Formatter.formatIpAddress(ip));
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    private void shareWifiADBInfo(String ip) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                String.format(getString(R.string.intent_extra_wifi_adb), ip)
        );
        shareIntent.setType(getString(R.string.intent_type_text_plain));
        startActivity(shareIntent);
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
                BuildConfig.VERSION_NAME
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
