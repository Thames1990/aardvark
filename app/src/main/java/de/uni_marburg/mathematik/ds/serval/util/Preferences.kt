package de.uni_marburg.mathematik.ds.serval.util

import android.graphics.Color
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

/** Is used to store key/value pairs permanently. */
object Preferences : KPref() {

    var accentColor: Int by kpref("ACCENT_COLOR", 0xff40c4ff.toInt())
    var backgroundColor: Int by kpref("BG_COLOR", Color.WHITE)
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_TIME_LAUNCH", true)
    var showChangelog: Boolean by kpref("SHOW_CHANGELOG", true)
    var textColor: Int by kpref("TEXT_COLOR", 0xff000000.toInt())
    var useBottomSheetDialogs: Boolean by kpref("USE_BOTTOM_SHEET_DIALOGS", true)
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)
    var version: Int by kpref("LAST_KNOWN_VERSION_CODE", 1)
}
