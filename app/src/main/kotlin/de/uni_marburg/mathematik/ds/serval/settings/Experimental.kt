package de.uni_marburg.mathematik.ds.serval.settings

import android.content.Context
import android.net.wifi.WifiManager
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefItemBase
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.shareText
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.toast
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.isDebugBuild
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag
import java.io.DataOutputStream
import java.lang.Runtime.getRuntime
import java.math.BigInteger
import java.net.InetAddress

object ExperimentalPrefs : KPref() {
    var enabled: Boolean by kpref(key = "EXPERIMENTAL_SETTINGS_ENABLED", fallback = isDebugBuild)
    var secureApp: Boolean by kpref(key = "SECURE_APP", fallback = false)
    var wifiADBEnabled: Boolean by kpref(key = "WIFI_ADB_ENABLED", fallback = false)
    var vibrationsEnabled: Boolean by kpref(key = "VIBRATIONS_ENABLED", fallback = false)
    var viewpagerSwipeEnabled: Boolean by kpref(key = "VIEWPAGER_SWIPE_ENABLED", fallback = false)
}

fun SettingsActivity.experimentalItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    plainText(
        title = R.string.preference_experimental_disclaimer,
        builder = { descRes = R.string.preference_experimental_disclaimer_desc }
    )

    header(title = R.string.preference_experimental_wifi_adb)

    fun toggleWiFiADB(activate: Boolean) {
        val runtime: Runtime = getRuntime()
        val superUser: Process = runtime.exec("su")
        val outputStream = DataOutputStream(superUser.outputStream)
        with(outputStream) {
            if (activate) writeBytes(string(R.string.preference_experimental_wifi_adb_activate))
            else writeBytes(string(R.string.preference_experimental_wifi_adb_deactivate))
            writeBytes(string(R.string.preference_experimental_wifi_adb_stop))
            writeBytes(string(R.string.preference_experimental_wifi_adb_start))
            flush()
            close()
        }
        superUser.waitFor()
    }

    fun shareWifiAdbCommand() {
        val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val ipAddress: Int = wifiManager.connectionInfo.ipAddress
        val addr: ByteArray = BigInteger.valueOf(ipAddress.toLong()).toByteArray().reversedArray()
        val hostAddress: String = InetAddress.getByAddress(addr).hostAddress
        shareText("adb connect $hostAddress")
    }

    checkbox(
        title = R.string.preference_experimental_enable_wifi_adb,
        getter = ExperimentalPrefs::wifiADBEnabled,
        setter = { wifiADBEnabled ->
            ExperimentalPrefs.wifiADBEnabled = wifiADBEnabled
            toggleWiFiADB(activate = wifiADBEnabled)
            reloadByTitle(R.string.preference_experimental_share_wifi_adb_command)
        }
    )

    plainText(
        title = R.string.preference_experimental_share_wifi_adb_command,
        builder = {
            descRes = R.string.preference_experimental_share_adb_command_desc
            enabler = ExperimentalPrefs::wifiADBEnabled
            onDisabledClick = { toast(string(R.string.preference_experimental_requires_wifi_adb)) }
            onClick = { shareWifiAdbCommand() }
        }
    )

    header(R.string.preference_experimental_security_header)

    checkbox(
        title = R.string.preference_experimental_secure_app,
        getter = ExperimentalPrefs::secureApp,
        setter = { secureApp ->
            ExperimentalPrefs.secureApp = secureApp
            setSecureFlag()
            shouldRestartApplication()
            reload()
        },
        builder = { descRes = R.string.preference_experimental_secure_app_desc }
    )

    fun KPrefItemBase.BaseContract<Boolean>.dependsOnSecurePrivacy() {
        enabler = ExperimentalPrefs::secureApp
        onDisabledClick = { toast(R.string.preference_experimental_requires_secure_privacy) }
    }

    checkbox(
        title = R.string.preference_experimental_vibration,
        getter = ExperimentalPrefs::vibrationsEnabled,
        setter = { ExperimentalPrefs.vibrationsEnabled = it },
        builder = {
            dependsOnSecurePrivacy()
            descRes = R.string.preference_experimental_vibration_desc
            shouldRestartApplication()
        }
    )

    header(R.string.preference_experimental_miscellaneous_header)

    checkbox(
        title = R.string.preference_experimental_paging,
        getter = ExperimentalPrefs::viewpagerSwipeEnabled,
        setter = { viewpagerSwipeEnabled ->
            ExperimentalPrefs.viewpagerSwipeEnabled = viewpagerSwipeEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_experimental_paging_desc }
    )

}