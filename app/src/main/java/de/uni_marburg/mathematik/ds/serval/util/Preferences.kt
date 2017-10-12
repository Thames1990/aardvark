package de.uni_marburg.mathematik.ds.serval.util

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

object Preferences : KPref() {
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_LAUNCH", true)
    var showChangelog: Boolean by kpref("SHOW_CHANGELOG", true)
    var useBottomSheetDialogs: Boolean by kpref("USE_BOTTOM_SHEET_DIALOGS", true)
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)
    var version: Int by kpref("VERSION", 1)
}
