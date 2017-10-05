package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.content.Context
import android.content.Intent
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.text.format.Formatter
import ca.allanwang.kau.email.sendEmail
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import java.io.DataOutputStream
import java.util.*

class SettingsFragment :
        PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    override fun onDestroy() {
        super.onDestroy()
        Aardvark.refWatcher.watch(this)
    }


    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.pref_main)
        createGeneralPreferences()
        createDebugPreferences()
        createAboutPreferences()
    }

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean {
        val isChecked = newValue as Boolean

        when (preference.key) {
            getString(R.string.preference_show_changelog) -> Preferences.showChangelog = isChecked
            getString(R.string.preference_use_bottom_sheets) -> Preferences.useBottomSheetDialogs = isChecked
            getString(R.string.preference_confirm_exit) -> Preferences.confirmExit = isChecked
            getString(R.string.preference_enable_wifi_adb) -> enableWifiAdb()
            else -> return false
        }

        return true
    }

    override fun onPreferenceClick(preference: Preference): Boolean {
        when (preference.key) {
            getString(R.string.preference_send_feedback) -> sendFeedback()
            else -> return false
        }
        return true
    }

    private fun createGeneralPreferences() {
        findPreference(getString(R.string.preference_show_changelog)).onPreferenceChangeListener = this
        findPreference(getString(R.string.preference_use_bottom_sheets)).onPreferenceChangeListener = this
        findPreference(getString(R.string.preference_confirm_exit)).onPreferenceChangeListener = this
    }

    private fun createDebugPreferences() {
        if (BuildConfig.DEBUG) {
            addPreferencesFromResource(R.xml.pref_debug)
            findPreference(getString(R.string.preference_enable_wifi_adb)).onPreferenceClickListener = this
        }
    }
    private fun createAboutPreferences() {
        findPreference(getString(R.string.preference_send_feedback)).onPreferenceClickListener = this
        findPreference(getString(R.string.preference_version)).summary = BuildConfig.VERSION_NAME
    }

    private fun enableWifiAdb() {
        val root = Runtime.getRuntime().exec("su")
        val dos = DataOutputStream(root.outputStream)
        dos.writeBytes("setprop service.adb.tcp.port 5555\n")
        dos.writeBytes("stop adbd\n")
        dos.writeBytes("start adbd\n")
        dos.writeBytes("exit\n")
        dos.flush()
        dos.close()
        root.waitFor()

        val mWifiManager = context.applicationContext
                .getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ip = mWifiManager.connectionInfo.ipAddress

        shareWifiADBInfo(Formatter.formatIpAddress(ip))
    }

    private fun shareWifiADBInfo(ip: String) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.putExtra(
                Intent.EXTRA_TEXT,
                String.format(getString(R.string.intent_extra_wifi_adb), ip)
        )
        shareIntent.type = getString(R.string.intent_type_text_plain)
        startActivity(shareIntent)
    }

    private fun sendFeedback() = activity.sendEmail(
            getString(R.string.email_adress_feedback),
            String.format(
                    Locale.getDefault(),
                    getString(R.string.intent_extra_query_from),
                    getString(R.string.app_name)
            )
    )
}
