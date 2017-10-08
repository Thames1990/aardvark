package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.support.v7.preference.Preference
import android.support.v7.preference.PreferenceFragmentCompat
import android.text.format.Formatter
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.utils.shareText
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.util.WIFI_ADB_PORT
import de.uni_marburg.mathematik.ds.serval.util.consume
import java.io.DataOutputStream
import java.util.*

class SettingsFragment :
        PreferenceFragmentCompat(),
        Preference.OnPreferenceClickListener,
        Preference.OnPreferenceChangeListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Aardvark.firebaseAnalytics.setCurrentScreen(activity, this::class.java.simpleName, null)
    }

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

    override fun onPreferenceChange(preference: Preference, newValue: Any): Boolean =
            with(Preferences) {
                when (preference.key) {
                    getString(R.string.preference_confirm_exit) ->
                        consume { confirmExit = newValue as Boolean }
                    getString(R.string.preference_show_changelog) ->
                        consume { showChangelog = newValue as Boolean }
                    getString(R.string.preference_use_bottom_sheets) ->
                        consume { useBottomSheetDialogs = newValue as Boolean }
                    else -> false
                }
            }

    override fun onPreferenceClick(preference: Preference) = when (preference.key) {
        getString(R.string.preference_enable_wifi_adb) -> consume { enableWifiAdb() }
        getString(R.string.preference_send_feedback) -> consume { sendFeedback() }
        else -> false
    }

    private fun createGeneralPreferences() {
        findPreference(getString(R.string.preference_confirm_exit)).onPreferenceChangeListener = this
        findPreference(getString(R.string.preference_show_changelog)).onPreferenceChangeListener = this
        findPreference(getString(R.string.preference_use_bottom_sheets)).onPreferenceChangeListener = this
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
        with(Runtime.getRuntime().exec("su")) {
            with(DataOutputStream(outputStream)) {
                writeBytes("setprop service.adb.tcp.port $WIFI_ADB_PORT\n")
                writeBytes("tcpip $WIFI_ADB_PORT\n")
                writeBytes("exit\n")
                flush()
                close()
            }
            waitFor()
        }

        with(context.getSystemService(Context.WIFI_SERVICE) as WifiManager) {
            context.shareText("adb connect " + Formatter.formatIpAddress(connectionInfo.ipAddress))
        }
    }

    private fun sendFeedback() = context.sendEmail(
            getString(R.string.email_adress_feedback),
            String.format(
                    Locale.getDefault(),
                    getString(R.string.intent_extra_query_from),
                    getString(R.string.app_name)
            )
    )
}
