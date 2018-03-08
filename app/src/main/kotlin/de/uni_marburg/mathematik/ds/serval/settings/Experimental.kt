package de.uni_marburg.mathematik.ds.serval.settings

import android.content.Context
import android.net.wifi.WifiManager
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefItemBase
import ca.allanwang.kau.utils.shareText
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed
import java.io.DataOutputStream
import java.lang.Runtime.getRuntime
import java.math.BigInteger
import java.net.InetAddress

fun SettingsActivity.experimentalItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    plainText(
        title = R.string.preference_debug_disclaimer,
        builder = { descRes = R.string.preference_debug_disclaimer_desc }
    )

    header(R.string.preference_debug_wifi_adb)

    checkbox(
        title = R.string.preference_enable_wifi_adb,
        getter = Prefs::useWifiADB,
        setter = { enableWifiADB ->
            Prefs.useWifiADB = enableWifiADB
            if (enableWifiADB) enableWifiAdb()
            else disableWifiAdb()
            reloadByTitle(R.string.preference_share_wifi_adb_command)
        }
    )

    plainText(
        title = R.string.preference_share_wifi_adb_command,
        builder = {
            descRes = R.string.preference_share_adb_command_desc
            enabler = Prefs::useWifiADB
            onDisabledClick = {
                snackbarThemed(getString(R.string.preference_enable_wifi_adb_hint))
            }
            onClick = { shareWifiAdbCommand() }
        }
    )

    header(R.string.preference_debug_security)

    checkbox(
        title = R.string.preference_secure_app,
        getter = Prefs::secureApp,
        setter = { secure_app ->
            Prefs.secureApp = secure_app
            setSecureFlag()
            shouldRestartApplication()
            reload()
        },
        builder = { descRes = R.string.preference_secure_app_desc }
    )

    fun KPrefItemBase.BaseContract<Boolean>.dependsOnSecurePrivacy() {
        enabler = Prefs::secureApp
        onDisabledClick = { snackbarThemed(R.string.preference_requires_secure_privacy) }
    }

    checkbox(
        title = R.string.preference_vibration,
        getter = Prefs::useVibrations,
        setter = { Prefs.useVibrations = it },
        builder = {
            dependsOnSecurePrivacy()
            descRes = R.string.preference_vibration_desc
            shouldRestartApplication()
        }
    )

    header(R.string.preference_debug_miscellaneous)

    checkbox(
        title = R.string.preference_progress_bar,
        getter = Prefs::showDownloadProgress,
        setter = { Prefs.showDownloadProgress = it },
        builder = {
            descRes = R.string.preference_progress_bar_desc
            shouldRestartMain()
        }
    )

    checkbox(
        title = R.string.preference_paging,
        getter = Prefs::viewpagerSwipe,
        setter = { usePaging ->
            Prefs.viewpagerSwipe = usePaging
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_paging_desc }
    )

}

/** Enables WifiADB and lets the user send ADB connection information. */
private fun enableWifiAdb() {
    with(getRuntime().exec("su")) {
        DataOutputStream(outputStream).apply {
            writeBytes("setprop service.adb.tcp.port 5555\\nstop adbd\\nstart adbd\\nexit")
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
    with(getRuntime().exec("su")) {
        DataOutputStream(outputStream).apply {
            writeBytes("setprop service.adb.tcp.port -1\\nstop adbd\\nstart adbd\\nexit")
            flush()
            close()
        }
        waitFor()
    }
}

/** Share information to connect to the device via WifiADB. */
private fun SettingsActivity.shareWifiAdbCommand() {
    val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    val ipAddress: Int = wifiManager.connectionInfo.ipAddress
    val addr: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray().reversedArray()
    val hostAddress: String = InetAddress.getByAddress(addr).hostAddress
    shareText("adb connect $hostAddress")
}