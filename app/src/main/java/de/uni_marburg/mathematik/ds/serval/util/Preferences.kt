package de.uni_marburg.mathematik.ds.serval.util

import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref

/** Is used to store key/value pairs permanently. */
object Preferences : KPref() {

    var bottomNavigationSelectedItemId: Int by kpref("BOTTOM_NAVIGATION_SELECTED_ITEM_ID", 1)
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_TIME_LAUNCH", true)
    var gridLayoutManagerSpanCount: Int by kpref("GRID_LAYOUT_MANAGER_SPAN_COUNT", 2)
    var lastKnownVersionCode: Int by kpref("LAST_KNOWN_VERSION_CODE", 1)
    var showChangelog: Boolean by kpref("SHOW_CHANGELOG", true)
    var staggeredGridLayoutManagerSpanCount: Int by kpref("STAGGERED_GRID_LAYOUT_MANAGER_SPAN_COUNT", 2)
    var trackLocation: Boolean by kpref("TRACK_LOCATION", false)
    var useBottomSheetDialogs: Boolean by kpref("USE_BOTTOM_SHEET_DIALOGS", true)
    var useGridLayoutManager: Boolean by kpref("USE_GRID_LAYOUT_MANAGER", false)
    var useLinearLayoutManager: Boolean by kpref("USE_LINEAR_LAYOUT_MANAGER", true)
    var useStaggeredGridLayoutManager: Boolean by kpref("USE_STAGGERED_GRID_LAYOUT_MANAGER", false)
}

