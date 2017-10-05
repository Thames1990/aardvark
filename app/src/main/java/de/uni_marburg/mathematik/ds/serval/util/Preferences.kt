package de.uni_marburg.mathematik.ds.serval.util

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

/** Is used to store key/value pairs permanently. */
object Preferences : KPref() {

    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_TIME_LAUNCH", true)
    var lastKnownVersionCode: Int by kpref("LAST_KNOWN_VERSION_CODE", 1)
    var showChangelog: Boolean by kpref("SHOW_CHANGELOG", true)
    var useBottomSheetDialogs: Boolean by kpref("USE_BOTTOM_SHEET_DIALOGS", true)
}

