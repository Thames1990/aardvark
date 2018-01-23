package de.uni_marburg.mathematik.ds.serval.utils

import android.graphics.Color
import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.isColorVisibleOn
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestPriority
import de.uni_marburg.mathematik.ds.serval.enums.MainActivityLayout
import de.uni_marburg.mathematik.ds.serval.enums.Theme

object Prefs : KPref() {

    val aardvarkId: String
        get() = "$installDate-$identifier"

    private val themeLoader = lazyResettable { Theme.values[theme] }
    private val t: Theme by themeLoader

    val accentColor: Int
        get() = t.accentColor
    val accentColorForWhite: Int
        get() = when {
            accentColor.isColorVisibleOn(Color.WHITE) -> accentColor
            textColor.isColorVisibleOn(Color.WHITE) -> textColor
            else -> Theme.AARDVARK_GREEN
        }
    val backgroundColor: Int
        get() = t.bgColor
    val headerColor: Int
        get() = t.headerColor
    val iconColor: Int
        get() = t.iconColor
    val isCustomTheme: Boolean
        get() = t == Theme.CUSTOM
    val textColor: Int
        get() = t.textColor

    val locationRequestPriority: LocationRequestPriority
        get() = LocationRequestPriority(locationRequestPriorityType)

    val mainActivityLayout: MainActivityLayout
        get() = MainActivityLayout(mainActivityLayoutType)

    var animate: Boolean by kpref("ANIMATE", true)
    var analytics: Boolean by kpref("USE_ANALYTICS", true)
    var changelog: Boolean by kpref("SHOW_CHANGELOG", true)
    var customTextColor: Int by kpref("COLOR_TEXT", 0xffeceff1.toInt())
    var customAccentColor: Int by kpref("COLOR_ACCENT", 0xff0288d1.toInt())
    var customBackgroundColor: Int by kpref("COLOR_BACKGROUND", 0xff212121.toInt())
    var customHeaderColor: Int by kpref("COLOR_HEADER", 0xff01579b.toInt())
    var customIconColor: Int by kpref("COLOR_ICONS", 0xffeceff1.toInt())
    var debugSettings: Boolean by kpref("DEBUG_SETTINGS", BuildConfig.DEBUG)
    var eventCount: Int by kpref("EVENT_COUNT", 10000)
    var exitConfirmation: Boolean by kpref("CONFIRM_EXIT", true)
    var identifier: Int by kpref("IDENTIFIER", -1)
    var installDate: Long by kpref("INSTALL_DATE", -1L)
    var isFirstLaunch: Boolean by kpref("IS_FIRST_LAUNCH", true)
    var isLoggedIn: Boolean by kpref("IS_LOGGED_IN", false)
    var kervalBaseUrl: String by kpref("KERVAL_BASE_URL", "serval.splork.de")
    var kervalPassword: String by kpref("KERVAL_PASSWORD", "pum123")
    var kervalPort: Int by kpref("KERVAL_PORT", 80)
    var kervalUser: String by kpref("KERVAL_USER", "pum")
    var lastLaunch: Long by kpref("LAST_LAUNCH", -1L)
    var locationRequestInterval: Int by kpref("LOCATION_REQUEST_INTERVAL", 10)
    var locationRequestFastestInterval: Int by kpref("LOCATION_REQUEST_FASTEST_INTERVAL", 5)
    var locationRequestPriorityType: Int by kpref("LOCATION_REQUEST_PRIORITY", 0)
    var mainActivityLayoutType: Int by kpref("MAIN_ACTIVITY_LAYOUT_TYPE", 1)
    var theme: Int by kpref("THEME", 0, postSetter = { _: Int -> themeLoader.invalidate() })
    var tintNavBar: Boolean by kpref("TINT_NAV_BAR", true)
    var secure_app: Boolean by kpref("USE_SECURE_FLAG", false)
    var useWifiADB: Boolean by kpref("USE_WIFI_ADB", false)
    var versionCode: Int by kpref("VERSION", -1)
}
