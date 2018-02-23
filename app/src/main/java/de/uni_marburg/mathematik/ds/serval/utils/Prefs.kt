package de.uni_marburg.mathematik.ds.serval.utils

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestPriority
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.enums.MapsStyle
import de.uni_marburg.mathematik.ds.serval.enums.Theme

object Prefs : KPref() {

    const val EVENT_COUNT = 10000

    private val mapsStyleLoader = lazyResettable { MapsStyle.values()[mapsStyleType] }
    val mapsStyle: MapsStyle by mapsStyleLoader

    private val themeLoader = lazyResettable { Theme.values()[themeType] }
    val theme: Theme by themeLoader

    val accentColor: Int
        get() = theme.accentColor
    val backgroundColor: Int
        get() = theme.backgroundColor
    val headerColor: Int
        get() = theme.headerColor
    val iconColor: Int
        get() = theme.iconColor
    val isCustomTheme: Boolean
        get() = theme == Theme.CUSTOM
    val textColor: Int
        get() = theme.textColor

    val locationRequestPriority: LocationRequestPriority
        get() = LocationRequestPriority(locationRequestPriorityType)

    val mainActivityLayout: MainActivityLayout
        get() = MainActivityLayout(mainActivityLayoutType)

    var animate: Boolean by kpref(key = "ANIMATE", fallback = true)
    var analytics: Boolean by kpref(key = "USE_ANALYTICS", fallback = true)
    var changelog: Boolean by kpref(key = "SHOW_CHANGELOG", fallback = true)
    var customTextColor: Int by kpref(key = "COLOR_TEXT", fallback = Theme.PORCELAIN)
    var customAccentColor: Int by kpref(key = "COLOR_ACCENT", fallback = Theme.LOCHMARA)
    var customBackgroundColor: Int by kpref(key = "COLOR_BACKGROUND", fallback = Theme.MINE_SHAFT)
    var customHeaderColor: Int by kpref(key = "COLOR_HEADER", fallback = Theme.BAHAMA_BLUE)
    var customIconColor: Int by kpref(key = "COLOR_ICONS", fallback = Theme.PORCELAIN)
    var debugSettings: Boolean by kpref(key = "DEBUG_SETTINGS", fallback = BuildConfig.DEBUG)
    var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    var exitConfirmation: Boolean by kpref("CONFIRM_EXIT", true)
    var mapsStyleType: Int by kpref(
        key = "MAPS_STYLE_TYPE",
        fallback = 0,
        postSetter = { _: Int -> mapsStyleLoader.invalidate() }
    )
    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var kervalBaseUrl: String by kpref(key = "KERVAL_BASE_URL", fallback = "serval.splork.de")
    var kervalPassword: String by kpref(key = "KERVAL_PASSWORD", fallback = "pum123")
    var kervalPort: Int by kpref(key = "KERVAL_PORT", fallback = 80)
    var kervalUser: String by kpref("KERVAL_USER", "pum")
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var locationRequestInterval: Long by kpref(key = "LOCATION_REQUEST_INTERVAL", fallback = 60L)
    var locationRequestFastestInterval: Long by kpref(
        key = "LOCATION_REQUEST_FASTEST_INTERVAL",
        fallback = 10L
    )
    var locationRequestPriorityType: Int by kpref(key = "LOCATION_REQUEST_PRIORITY", fallback = 0)
    var mainActivityLayoutType: Int by kpref(
        key = "MAIN_ACTIVITY_LAYOUT_TYPE",
        fallback = MainActivityLayout.TOP_BAR.ordinal
    )
    var themeType: Int by kpref(
        key = "THEME",
        fallback = 0,
        postSetter = { _: Int -> themeLoader.invalidate() }
    )
    var tintNavBar: Boolean by kpref(key = "TINT_NAV_BAR", fallback = false)
    var secure_app: Boolean by kpref(key = "USE_SECURE_FLAG", fallback = false)
    var showDownloadProgress: Boolean by kpref(key = "USE_PROGRESS_BAR", fallback = false)
    var useVibration: Boolean by kpref(key = "USE_VIBRATION", fallback = false)
    var useWifiADB: Boolean by kpref(key = "USE_WIFI_ADB", fallback = false)
    var versionCode: Int by kpref(key = "VERSION", fallback = -1)
    var viewpagerSwipe: Boolean by kpref(key = "VIEWPAGER_SWIPE", fallback = false)
}
