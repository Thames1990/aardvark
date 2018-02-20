package de.uni_marburg.mathematik.ds.serval.settings

import android.net.wifi.WifiManager
import androidx.content.systemService
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
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

/** Created by thames1990 on 09.12.17. */
fun SettingsActivity.debugItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    plainText(R.string.debug_disclaimer_info)

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
            descRes = R.string.preference_share_adb_command_description
            enabler = Prefs::useWifiADB
            onDisabledClick = {
                snackbarThemed(getString(R.string.preference_enable_wifi_adb_hint))
            }
            onClick = { shareWifiAdbCommand() }
        }
    )

    checkbox(
        title = R.string.paging,
        getter = Prefs::viewpagerSwipe,
        setter = { usePaging ->
            Prefs.viewpagerSwipe = usePaging
            shouldRestartMain()
        },
        builder = { descRes = R.string.paging_description }
    )

    checkbox(
        title = R.string.secure_app,
        getter = Prefs::secure_app,
        setter = { secure_app ->
            Prefs.secure_app = secure_app
            setSecureFlag()
            shouldRestartApplication()
        },
        builder = { descRes = R.string.secure_app_desc }
    )

    checkbox(
        title = R.string.preference_progress_bar,
        getter = Prefs::useProgressBar,
        setter = { Prefs.useProgressBar = it },
        builder = {
            descRes = R.string.preference_progress_bar_description
            shouldRestartMain()
        }
    )

    checkbox(
        title = R.string.preference_vibration,
        getter = Prefs::useVibration,
        setter = { Prefs.useVibration = it },
        builder = {
            descRes = R.string.preference_vibration_description
            shouldRestartApplication()
        }
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
    val wifiManager = systemService<WifiManager>()
    val ipAdress: Int = wifiManager.connectionInfo.ipAddress
    val inetAdress = BigInteger
        .valueOf(ipAdress.toLong())
        .toByteArray()
        .reversedArray()
    val hostAdress = InetAddress.getByAddress(inetAdress).hostAddress
    shareText("adb connect $hostAdress")
}