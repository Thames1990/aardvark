package de.uni_marburg.mathematik.ds.serval.view.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import ca.allanwang.kau.about.kauLaunchAbout
import ca.allanwang.kau.email.sendEmail
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.finishSlideOut
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.string
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.settings.getBehaviourPrefs
import de.uni_marburg.mathematik.ds.serval.util.*
import de.uni_marburg.mathematik.ds.serval.util.Prefs.kervalPassword
import de.uni_marburg.mathematik.ds.serval.util.Prefs.kervalUser
import de.uni_marburg.mathematik.ds.serval.util.Prefs.useWifiADB
import org.jetbrains.anko.toast
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.InetAddress

class SettingsActivity : KPrefActivity() {

    var resultFlag = Activity.RESULT_CANCELED

    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {
        accentColor = { Prefs.colorAccent }
        textColor = { Prefs.textColor }
    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        if (Prefs.debugSettings) createDebugPreferences()
        subItems(R.string.preference_general, getBehaviourPrefs()) {
            descRes = R.string.behaviour_desc
            iicon = GoogleMaterial.Icon.gmd_settings
        }
        // TODO Modify other settings
        createThemePreferences()
        createServalPreferences()
        createAboutPreferences()
    }

    fun shouldRestartMain() {
        setAardvarkResult(MainActivity.REQUEST_RESTART)
    }

    @SuppressLint("MissingSuperCall")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSecureFlag()
        setCurrentScreen()
        animate = Prefs.animate
        themeExterior(false)
    }

    fun themeExterior(animate: Boolean = true) {
        if (animate) bgCanvas.fade(Prefs.bgColor)
        else bgCanvas.set(Prefs.bgColor)
        if (animate) toolbarCanvas.ripple(Prefs.headerColor, RippleCanvas.MIDDLE, RippleCanvas.END)
        else toolbarCanvas.set(Prefs.headerColor)
        aardvarkNavigationBar()
    }

    override fun onBackPressed() {
        if (!super.backPress()) {
            setResult(resultFlag)
            finishSlideOut()
        }
    }

    fun setAardvarkResult(flag: Int) {
        resultFlag = resultFlag or flag
    }

    private fun KPrefAdapterBuilder.createDebugPreferences() {
        header(R.string.preference_debug)
        checkbox(R.string.preference_enable_wifi_adb, { useWifiADB }, {
            useWifiADB = it
            when (it) {
                true  -> enableWifiAdb()
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

    private fun KPrefAdapterBuilder.createThemePreferences() {
        header(R.string.preference_theme)
        colorPicker(R.string.color_primary, { Prefs.colorPrimary }, {
            Prefs.colorPrimary = it
            reload()
            toolbarCanvas.ripple(it, RippleCanvas.MIDDLE, RippleCanvas.END, 500L)
        })
        colorPicker(R.string.color_accent, { Prefs.colorAccent }, {
            Prefs.colorAccent = it
            reload()
        })
        colorPicker(R.string.color_background, { Prefs.bgColor }, {
            Prefs.bgColor = it
            bgCanvas.ripple(it, duration = 500L)
        })
        colorPicker(R.string.color_text, { Prefs.textColor }, {
            Prefs.textColor = it
            reload()
        })
    }

    private fun KPrefAdapterBuilder.createServalPreferences() {
        header(R.string.preference_serval)
        text(R.string.username, { kervalUser }, { kervalUser = it }) {
            descRes = R.string.preference_username_description
            onClick = { itemView, _, item ->
                consume {
                    itemView.context.materialDialogThemed {
                        title(string(R.string.username))
                        input(string(R.string.username), item.pref, { _, input ->
                            item.pref = input.toString()
                        })
                    }
                }
            }
        }
        text(R.string.password, { kervalPassword }, { kervalPassword = it }) {
            descRes = R.string.preference_password_description
            onClick = { itemView, _, item ->
                consume {
                    itemView.context.materialDialogThemed {
                        title(string(R.string.password))
                        input(string(R.string.password), item.pref, { _, input ->
                            item.pref = input.toString()
                        })
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
            onClick = { _, _, _ -> consume { aardvarkChangelog() } }
        }
    }

    /**
     * Enables WifiADB and lets the user send ADB connection information.
     */
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
     * Disables WifiADB.
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

    /**
     * Share information to connect to the device via WifiADB.
     */
    private fun shareWifiAdbCommand() =
            with(applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager) {
                val ipAdress: ByteArray = BigInteger
                        .valueOf(connectionInfo.ipAddress.toLong())
                        .toByteArray()
                        .reversedArray()
                val hostAdress = InetAddress.getByAddress(ipAdress).hostAddress
                shareText(String.format(string(R.string.adb_connect), hostAdress))
            }

    /**
     * Opens an email client with device information for the user to send feedback.
     */
    private fun sendFeedback() = sendEmail(
            string(R.string.email_adress_feedback),
            String.format(string(R.string.intent_extra_query_from), string(R.string.aardvark_name))
    )

}