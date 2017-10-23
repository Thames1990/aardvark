package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.WindowManager
import ca.allanwang.kau.about.kauLaunchAbout
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.swipe.SWIPE_EDGE_LEFT
import ca.allanwang.kau.swipe.kauSwipeFinish
import ca.allanwang.kau.swipe.kauSwipeOnCreate
import ca.allanwang.kau.swipe.kauSwipeOnDestroy
import ca.allanwang.kau.utils.color
import ca.allanwang.kau.utils.materialDialog
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.xml.showChangelog
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.util.Preferences.confirmExit
import de.uni_marburg.mathematik.ds.serval.util.Preferences.kervalPassword
import de.uni_marburg.mathematik.ds.serval.util.Preferences.kervalUser
import de.uni_marburg.mathematik.ds.serval.util.Preferences.showChangelog
import de.uni_marburg.mathematik.ds.serval.util.Preferences.useAnalytics
import de.uni_marburg.mathematik.ds.serval.util.Preferences.useWifiADB
import de.uni_marburg.mathematik.ds.serval.util.WIFI_ADB_PORT
import de.uni_marburg.mathematik.ds.serval.util.consume
import org.jetbrains.anko.toast
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.InetAddress
import java.util.*

class PreferenceActivity : KPrefActivity() {

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        bgCanvas.set(color(android.R.color.white))
        toolbarCanvas.set(color(R.color.color_primary))
        Aardvark.firebaseAnalytics.setCurrentScreen(this, this::class.java.simpleName, null)
        kauSwipeOnCreate { edgeFlag = SWIPE_EDGE_LEFT }
    }

    override fun onDestroy() {
        kauSwipeOnDestroy()
        super.onDestroy()
    }

    override fun onBackPressed() {
        kauSwipeFinish()
    }

    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {
        accentColor = { color(R.color.color_accent) }
        textColor = { color(R.color.color_text) }
    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        if (BuildConfig.DEBUG) createDebugPreferences()
        createGeneralPreferences()
        createServalPreferences()
        createAboutPreferences()
    }

    private fun KPrefAdapterBuilder.createDebugPreferences() {
        header(R.string.preference_debug)
        checkbox(R.string.preference_enable_wifi_adb, { useWifiADB }, {
            useWifiADB = it
            when (it) {
                true -> enableWifiAdb()
                false -> disableWifiAdb()
            }
            reloadByTitle(R.string.preference_share_wifi_adb_command)
        })
        plainText(R.string.preference_share_wifi_adb_command) {
            descRes = R.string.preference_share_adb_command_description
            enabler = { useWifiADB }
            onDisabledClick = { itemView, _, _ ->
                consume { itemView.context.toast(getString(R.string.preference_enable_wifi_adb_hint)) }
            }
            onClick = { _, _, _ -> consume { shareWifiAdbCommand() } }
        }
    }

    private fun KPrefAdapterBuilder.createGeneralPreferences() {
        header(R.string.preference_general)
        checkbox(R.string.preference_show_changelog, { showChangelog }, { showChangelog = it })
        checkbox(R.string.preference_confirm_exit, { confirmExit }, { confirmExit = it })
        checkbox(R.string.preference_use_analytics, { useAnalytics }, { useAnalytics = it }) {
            descRes = R.string.preference_use_analytics_description
        }
    }

    private fun KPrefAdapterBuilder.createServalPreferences() {
        header(R.string.preference_serval)
        text(R.string.username, { kervalUser }, { kervalUser = it }) {
            descRes = R.string.preference_username_description
            onClick = { itemView, _, item ->
                consume {
                    itemView.context.materialDialog {
                        title(string(R.string.username))
                        input(string(R.string.username), item.pref, { _, input -> item.pref = input.toString() })
                        inputRange(1, 20)
                    }
                }
            }
        }
        text(R.string.password, { kervalPassword }, { kervalPassword = it }) {
            descRes = R.string.preference_password_description
            onClick = { itemView, _, item ->
                consume {
                    itemView.context.materialDialog {
                        title(string(R.string.password))
                        input(string(R.string.password), item.pref, { _, input ->
                            item.pref = input.toString()
                        })
                        inputRange(1, 20)
                    }
                }
            }
        }
    }

    private fun KPrefAdapterBuilder.createAboutPreferences() {
        header(R.string.preference_about)
        plainText(R.string.preference_send_feedback) {
            descRes = R.string.preference_send_feedback_description
            onClick = { _, _, _ -> consume { sendFeedback() } }
        }
        plainText(R.string.preference_faq) {
            descRes = R.string.preference_faq_description
            onClick = { _, _, _ ->
                kauLaunchAbout(AboutActivity::class.java)
                false
            }
        }
        plainText(R.string.preference_privacy_policy)
        plainText(R.string.preference_terms_and_conditions)
        plainText(R.string.preference_version) {
            descRes = R.string.app_version
            onClick = { _, _, _ ->
                consume {
                    showChangelog(R.xml.changelog) {
                        title(R.string.kau_changelog)
                        positiveText(android.R.string.ok)
                    }
                }
            }
        }
    }

    private fun enableWifiAdb() {
        with(Runtime.getRuntime().exec(string(R.string.adb_superuser))) {
            with(DataOutputStream(outputStream)) {
                writeBytes(String.format(string(R.string.adb_wifi_enable), WIFI_ADB_PORT))
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
        with(Runtime.getRuntime().exec(string(R.string.adb_superuser))) {
            with(DataOutputStream(outputStream)) {
                writeBytes(string(R.string.adb_wifi_disable))
                flush()
                close()
            }
            waitFor()
        }
    }

    private fun shareWifiAdbCommand() =
            with(applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager) {
                val ipAdress: ByteArray = BigInteger
                        .valueOf(connectionInfo.ipAddress.toLong())
                        .toByteArray()
                        .reversedArray()
                val hostAdress = InetAddress.getByAddress(ipAdress).hostAddress
                shareText(String.format(string(R.string.adb_connect), hostAdress))
            }

    private fun sendFeedback() = sendEmail(
            string(R.string.email_adress_feedback),
            String.format(
                    Locale.getDefault(),
                    string(R.string.intent_extra_query_from),
                    string(R.string.app_name)
            )
    )

}