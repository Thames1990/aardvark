package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.text.format.Formatter
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.startActivity
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Preferences.confirmExit
import de.uni_marburg.mathematik.ds.serval.util.Preferences.showChangelog
import de.uni_marburg.mathematik.ds.serval.util.Preferences.useBottomSheetDialogs
import de.uni_marburg.mathematik.ds.serval.util.Preferences.useWifiADB
import de.uni_marburg.mathematik.ds.serval.util.WIFI_ADB_PORT
import de.uni_marburg.mathematik.ds.serval.util.consume
import org.jetbrains.anko.toast
import java.io.DataOutputStream
import java.util.*

class PreferenceActivity : KPrefActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Aardvark.firebaseAnalytics.setCurrentScreen(this, this::class.java.simpleName, null)
    }


    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {


    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        if (BuildConfig.DEBUG) createDebugPreferences()
        createGeneralPreferences()
        createAboutPreferences()
    }

    private fun KPrefAdapterBuilder.createGeneralPreferences() {
        header(R.string.preference_general)
        checkbox(R.string.preference_show_changelog, { showChangelog }, { showChangelog = it })
        checkbox(
                R.string.preference_use_bottom_sheets,
                { useBottomSheetDialogs },
                { useBottomSheetDialogs = it }
        ) { descRes = R.string.preference_use_bottom_sheets_description }
        checkbox(R.string.confirm_exit, { confirmExit }, { confirmExit = it })
    }

    private fun KPrefAdapterBuilder.createDebugPreferences() {
        header(R.string.preference_debug)
        checkbox(R.string.preference_enable_wifi_adb, { useWifiADB }, {
            useWifiADB = it
            when (it) {
                true -> enableWifiAdb()
                false -> disableWifiAdb()
            }
            reloadByTitle(R.string.preference_share_adb_command)
        })
        plainText(R.string.preference_share_adb_command) {
            descRes = R.string.preference_share_adb_command_description
            enabler = { useWifiADB }
            onDisabledClick = { itemView, _, _ ->
                consume {
                    itemView.context.toast(getString(R.string.preference_enable_wifi_adb_hint))
                }
            }
            onClick = { _, _, _ -> consume { shareIpAddress() } }
        }
    }

    private fun KPrefAdapterBuilder.createAboutPreferences() {
        header(R.string.preference_about)
        plainText(R.string.preference_send_feedback) {
            descRes = R.string.preference_faq_description
            onClick = { _, _, _ -> consume { sendFeedback() } }
        }
        plainText(R.string.preference_faq)
        plainText(R.string.preference_privacy_policy)
        plainText(R.string.preference_terms_and_conditions)
        plainText(R.string.preference_version) {
            descRes = R.string.app_version
            onClick = { _, _, _ ->
                startActivity(ChangelogActivity::class.java, transition = true)
                false
            }
        }
    }

    private fun enableWifiAdb() {
        with(Runtime.getRuntime().exec(string(R.string.superuser))) {
            with(DataOutputStream(outputStream)) {
                writeBytes(String.format(string(R.string.enable_wifi_adb), WIFI_ADB_PORT))
                flush()
                close()
            }
            waitFor()
        }
    }

    /**
     * TODO Figure out how to deactivate WifiADB on isFinishing
     */
    private fun disableWifiAdb() {
        with(Runtime.getRuntime().exec(string(R.string.superuser))) {
            with(DataOutputStream(outputStream)) {
                writeBytes(string(R.string.disable_wifi_adb))
                flush()
                close()
            }
            waitFor()
        }
    }

    private fun shareIpAddress() {
        with(applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager) {
            shareText("adb connect " + Formatter.formatIpAddress(connectionInfo.ipAddress))
        }
    }

    private fun sendFeedback() = sendEmail(
            getString(R.string.email_adress_feedback),
            String.format(
                    Locale.getDefault(),
                    getString(R.string.intent_extra_query_from),
                    getString(R.string.app_name)
            )
    )

}