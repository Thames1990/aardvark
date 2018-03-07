package de.uni_marburg.mathematik.ds.serval.utils

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.enums.*

object Prefs : KPref() {

    const val EVENT_COUNT = 10000

    private val mapsStyleLoader = lazyResettable { MapsStyle.values()[mapsStyleIndex] }
    val mapsStyle: MapsStyle by mapsStyleLoader

    private val themeLoader = lazyResettable { Theme.values()[themeIndex] }
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

    val dateTimeFormat: DateTimeFormat
        get() = DateTimeFormat(dateTimeFormatIndex)

    val locationRequestAccuracy: LocationRequestAccuracy
        get() = LocationRequestAccuracy(locationRequestAccuracyIndex)

    val mainActivityLayout: MainActivityLayout
        get() = MainActivityLayout(mainActivityLayoutIndex)

    var animate: Boolean by kpref(key = "ANIMATE", fallback = true)
    var analytics: Boolean by kpref(key = "ANALYTICS", fallback = true)
    var showChangelog: Boolean by kpref(key = "SHOW_CHANGELOG", fallback = true)
    var customTextColor: Int by kpref(key = "CUSTOM_COLOR_TEXT", fallback = Theme.PORCELAIN)
    var customAccentColor: Int by kpref(key = "CUSTOM_COLOR_ACCENT", fallback = Theme.LOCHMARA)
    var customBackgroundColor: Int by kpref(
        key = "CUSTOM_COLOR_BACKGROUND",
        fallback = Theme.MINE_SHAFT
    )
    var customHeaderColor: Int by kpref(key = "CUSTOM_COLOR_HEADER", fallback = Theme.BAHAMA_BLUE)
    var customIconColor: Int by kpref(key = "CUSTOM_COLOR_ICONS", fallback = Theme.PORCELAIN)
    var dateTimeFormatIndex: Int by kpref(
        key = "DATE_TIME_FORMAT_INDEX",
        fallback = DateTimeFormat.MEDIUM_DATE_MEDIUM_TIME.ordinal,
        postSetter = { value: Int ->
            answersCustom(
                name = "Date time format",
                events = *arrayOf("Date time format" to DateTimeFormat(value).name)
            )
        }
    )
    var experimentalSettings: Boolean by kpref(
        key = "EXPERIMENTAL_SETTINGS",
        fallback = BuildConfig.DEBUG
    )
    var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)
    var mapsStyleIndex: Int by kpref(
        key = "MAPS_STYLE_INDEX",
        fallback = 0,
        postSetter = { value: Int ->
            mapsStyleLoader.invalidate()
            answersCustom(
                name = "Maps style",
                events = *arrayOf("Count" to MapsStyle(value).name)
            )
        }
    )
    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var kervalBaseUrl: String by kpref(key = "KERVAL_BASE_URL", fallback = "serval.splork.de")
    var kervalPassword: String by kpref(key = "KERVAL_PASSWORD", fallback = "pum123")
    var kervalPort: Int by kpref(key = "KERVAL_PORT", fallback = 80)
    var kervalUser: String by kpref("KERVAL_USER", "pum")
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var locationRequestInterval: Int by kpref(key = "LOCATION_REQUEST_INTERVAL", fallback = 2500)
    var locationRequestDistance: Int by kpref("LOCATION_REQUEST_DISTANCE", fallback = 150)
    var locationRequestAccuracyIndex: Int by kpref(
        key = "LOCATION_REQUEST_ACCURACY_INDEX",
        fallback = LocationRequestAccuracy.HIGH.ordinal
    )
    var mainActivityLayoutIndex: Int by kpref(
        key = "MAIN_ACTIVITY_LAYOUT_INDEX",
        fallback = MainActivityLayout.TOP_BAR.ordinal,
        postSetter = { value: Int ->
            answersCustom(
                name = "Main Layout",
                events = *arrayOf("Type" to MainActivityLayout(value).name)
            )
        }
    )
    var themeIndex: Int by kpref(
        key = "THEME_INDEX",
        fallback = Theme.LIGHT.ordinal,
        postSetter = { value: Int ->
            themeLoader.invalidate()
            answersCustom(name = "Theme", events = *arrayOf("Count" to Theme(value).name))
        }
    )
    var tintNavBar: Boolean by kpref(key = "TINT_NAV_BAR", fallback = false)
    var secure_app: Boolean by kpref(key = "SECURE_APP", fallback = false)
    var showDownloadProgress: Boolean by kpref(key = "SHOW_DOWNLOAD_PROGRESS", fallback = false)
    var useVibration: Boolean by kpref(key = "USE_VIBRATION", fallback = false)
    var useWifiADB: Boolean by kpref(key = "USE_WIFI_ADB", fallback = false)
    var versionCode: Int by kpref(key = "VERSION_CODE", fallback = -1)
    var viewpagerSwipe: Boolean by kpref(key = "VIEWPAGER_SWIPE", fallback = false)
}
