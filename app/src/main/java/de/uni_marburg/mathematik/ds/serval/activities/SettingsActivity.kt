package de.uni_marburg.mathematik.ds.serval.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import ca.allanwang.kau.kpref.activity.CoreAttributeContract
import ca.allanwang.kau.kpref.activity.KPrefActivity
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.ui.views.RippleCanvas
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.Support
import de.uni_marburg.mathematik.ds.serval.settings.getAppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.getBehaviourPrefs
import de.uni_marburg.mathematik.ds.serval.settings.getServalPrefs
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.utils.Prefs.useWifiADB
import org.jetbrains.anko.toast
import java.io.DataOutputStream
import java.math.BigInteger
import java.net.InetAddress

class SettingsActivity : KPrefActivity() {

    var resultFlag = Activity.RESULT_CANCELED

    override fun kPrefCoreAttributes(): CoreAttributeContract.() -> Unit = {
        accentColor = { Prefs.accentColor }
        textColor = { Prefs.textColor }
    }

    override fun onCreateKPrefs(savedInstanceState: Bundle?): KPrefAdapterBuilder.() -> Unit = {
        if (Prefs.debugSettings) createDebugPreferences()

        subItems(R.string.behaviour, getBehaviourPrefs()) {
            descRes = R.string.behaviour_desc
            iicon = GoogleMaterial.Icon.gmd_settings
        }
        subItems(R.string.appearance, getAppearancePrefs()) {
            descRes = R.string.appearance_desc
            iicon = GoogleMaterial.Icon.gmd_palette
        }
        subItems(R.string.serval, getServalPrefs()) {
            descRes = R.string.serval_desc
            iicon = GoogleMaterial.Icon.gmd_network_wifi
        }

        plainText(R.string.about_aardvark) {
            descRes = R.string.about_aardvark_desc
            iicon = GoogleMaterial.Icon.gmd_info
            onClick = { _, _, _ ->
                consume {
                    startActivityForResult(AboutActivity::class.java, 9, true)
                }
            }
        }
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean = consume {
        menuInflater.inflate(R.menu.menu_settings, menu)
        toolbar.tint(Prefs.iconColor)
        setMenuIcons(menu, Prefs.iconColor,
                R.id.action_email to GoogleMaterial.Icon.gmd_email,
                R.id.action_changelog to GoogleMaterial.Icon.gmd_info
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = consume {
        when (item.itemId) {
            R.id.action_email     -> materialDialogThemed {
                title(R.string.subject)
                items(Support.values().map { string(it.title) })
                itemsCallback { _, _, which, _ ->
                    Support.values()[which].sendEmail(this@SettingsActivity)
                }
            }
            R.id.action_changelog -> aardvarkChangelog()
            else                  -> return super.onOptionsItemSelected(item)
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

}