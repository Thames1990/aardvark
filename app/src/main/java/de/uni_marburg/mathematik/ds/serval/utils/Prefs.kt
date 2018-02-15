package de.uni_marburg.mathematik.ds.serval.utils

import android.graphics.Color
import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.isColorVisibleOn
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
    val accentColorForWhite: Int
        get() = when {
            accentColor.isColorVisibleOn(Color.WHITE) -> accentColor
            textColor.isColorVisibleOn(Color.WHITE) -> textColor
            else -> Theme.FRUIT_SALAD
        }
    val backgroundColor: Int
        get() = theme.bgColor
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

    var animate: Boolean by kpref("ANIMATE", true)
    var analytics: Boolean by kpref("USE_ANALYTICS", true)
    var changelog: Boolean by kpref("SHOW_CHANGELOG", true)
    var customTextColor: Int by kpref("COLOR_TEXT", Theme.PORCELAIN)
    var customAccentColor: Int by kpref("COLOR_ACCENT", Theme.LOCHMARA)
    var customBackgroundColor: Int by kpref("COLOR_BACKGROUND", Theme.MINE_SHAFT)
    var customHeaderColor: Int by kpref("COLOR_HEADER", Theme.BAHAMA_BLUE)
    var customIconColor: Int by kpref("COLOR_ICONS", Theme.PORCELAIN)
    var debugSettings: Boolean by kpref("DEBUG_SETTINGS", BuildConfig.DEBUG)
    var eventCount: Int by kpref("EVENT_COUNT", EVENT_COUNT)
    var exitConfirmation: Boolean by kpref("CONFIRM_EXIT", true)
    var mapsStyleType: Int by kpref(
        "MAPS_STYLE_TYPE",
        0,
        postSetter = { _: Int -> mapsStyleLoader.invalidate() })
    var installDate: Long by kpref("INSTALL_DATE", -1L)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_LAUNCH", true)
    var isLoggedIn: Boolean by kpref("IS_LOGGED_IN", false)
    var kervalBaseUrl: String by kpref("KERVAL_BASE_URL", "serval.splork.de")
    var kervalPassword: String by kpref("KERVAL_PASSWORD", "pum123")
    var kervalPort: Int by kpref("KERVAL_PORT", 80)
    var kervalUser: String by kpref("KERVAL_USER", "pum")
    var lastLaunch: Long by kpref("LAST_LAUNCH", -1L)
    var locationRequestInterval: Long by kpref("LOCATION_REQUEST_INTERVAL", 60L)
    var locationRequestFastestInterval: Long by kpref("LOCATION_REQUEST_FASTEST_INTERVAL", 10L)
    var locationRequestPriorityType: Int by kpref("LOCATION_REQUEST_PRIORITY", 0)
    var mainActivityLayoutType: Int by kpref("MAIN_ACTIVITY_LAYOUT_TYPE", 1)
    var themeType: Int by kpref("THEME", 0, postSetter = { _: Int -> themeLoader.invalidate() })
    var tintNavBar: Boolean by kpref("TINT_NAV_BAR", false)
    var secure_app: Boolean by kpref("USE_SECURE_FLAG", false)
    var usePaging: Boolean by kpref("USE_PAGING", false)
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)
    var versionCode: Int by kpref("VERSION", -1)
}
