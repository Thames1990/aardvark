package de.uni_marburg.mathematik.ds.serval.settings

import android.content.Context
import android.net.wifi.WifiManager
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.shareText
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.consume
import org.jetbrains.anko.toast
import java.io.DataOutputStream
import java.lang.Runtime.getRuntime
import java.math.BigInteger
import java.net.InetAddress

/** Created by thames1990 on 09.12.17. */
fun SettingsActivity.getDebugPrefs(): KPrefAdapterBuilder.() -> Unit = {
    plainText(R.string.experimental_disclaimer) {
        descRes = R.string.debug_disclaimer_info
    }
    checkbox(R.string.preference_enable_wifi_adb, { Prefs.useWifiADB }, {
        Prefs.useWifiADB = it
        when (it) {
            true -> enableWifiAdb()
            false -> disableWifiAdb()
        }
        reloadByTitle(R.string.preference_share_wifi_adb_command)
    })
    plainText(R.string.preference_share_wifi_adb_command) {
        descRes = R.string.preference_share_adb_command_description
        enabler = { Prefs.useWifiADB }
        onDisabledClick = { toast(getString(R.string.preference_enable_wifi_adb_hint)) }
        onClick = { shareWifiAdbCommand() }
    }
}

/** Enables WifiADB and lets the user send ADB connection information. */
private fun enableWifiAdb() {
    with(getRuntime().exec("su")) {
        with(DataOutputStream(outputStream)) {
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
        with(DataOutputStream(outputStream)) {
            writeBytes("setprop service.adb.tcp.port -1\\nstop adbd\\nstart adbd\\nexit")
            flush()
            close()
        }
        waitFor()
    }
}

/** Share information to connect to the device via WifiADB. */
private fun SettingsActivity.shareWifiAdbCommand() =
        with(applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager) {
            val ipAdress: ByteArray = BigInteger
                    .valueOf(connectionInfo.ipAddress.toLong())
                    .toByteArray()
                    .reversedArray()
            val hostAdress = InetAddress.getByAddress(ipAdress).hostAddress
            shareText("adb connect $hostAdress")
        }