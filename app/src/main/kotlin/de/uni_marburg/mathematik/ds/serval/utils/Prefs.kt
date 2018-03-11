package de.uni_marburg.mathematik.ds.serval.utils

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import de.uni_marburg.mathematik.ds.serval.enums.*
import kotlin.math.roundToInt

object Prefs : KPref() {

    const val EVENT_COUNT = 10000

    private val locationRequestAccuracyLoader =
        lazyResettable { LocationRequestAccuracy.values()[locationRequestAccuracyIndex] }
    val locationRequestAccuracy: LocationRequestAccuracy by locationRequestAccuracyLoader

    private val mapStyleLoader = lazyResettable { MapStyle.values()[mapStyleIndex] }
    val mapStyle: MapStyle by mapStyleLoader

    var animate: Boolean by kpref(
        key = "ANIMATE",
        fallback = true,
        postSetter = { value: Boolean ->
            logAnalytics(
                name = "Animations",
                events = *arrayOf("Animations" to value)
            )
        }
    )
    var analytics: Boolean by kpref(key = "ANALYTICS", fallback = true)
    var experimentalSettings: Boolean by kpref(
        key = "EXPERIMENTAL_SETTINGS",
        fallback = isDebugBuild
    )
    var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    var confirmExit: Boolean by kpref("CONFIRM_EXIT", true)
    var mapStyleIndex: Int by kpref(
        key = "MAPS_STYLE_INDEX",
        fallback = 0,
        postSetter = { value: Int ->
            mapStyleLoader.invalidate()
            logAnalytics(
                name = "Maps style",
                events = *arrayOf("Count" to MapStyle(value).name)
            )
        }
    )
    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var servalBaseUrl: String by kpref(key = "SERVAL_BASE_URL", fallback = "serval.splork.de")
    var servalPassword: String by kpref(key = "SERVAL_PASSWORD", fallback = "pum123")
    var servalPort: Int by kpref(key = "SERVAL_PORT", fallback = 80)
    var servalUser: String by kpref("SERVAL_USER", "pum")
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var locationRequestInterval: Int by kpref(
        key = "LOCATION_REQUEST_INTERVAL",
        fallback = arrayOf(
            LocationRequestAccuracy.LOCATION_REQUEST_MIN_INTERVAL,
            LocationRequestAccuracy.LOCATION_REQUEST_MAX_INTERVAL
        ).average().roundToInt()
    )
    var locationRequestDistance: Int by kpref(
        "LOCATION_REQUEST_DISTANCE",
        fallback = arrayOf(
            LocationRequestAccuracy.LOCATION_REQUEST_MIN_DISTANCE,
            LocationRequestAccuracy.LOCATION_REQUEST_MAX_DISTANCE
        ).average().roundToInt()
    )
    var locationRequestAccuracyIndex: Int by kpref(
        key = "LOCATION_REQUEST_ACCURACY_INDEX",
        fallback = LocationRequestAccuracy.HIGH.ordinal,
        postSetter = { locationRequestAccuracyLoader.invalidate() }
    )
    var tintNavBar: Boolean by kpref(key = "TINT_NAV_BAR", fallback = false)
    var secureApp: Boolean by kpref(key = "SECURE_APP", fallback = false)
    var showChangelog: Boolean by kpref(key = "SHOW_CHANGELOG", fallback = true)
    var showDownloadProgress: Boolean by kpref(key = "SHOW_DOWNLOAD_PROGRESS", fallback = false)
    var useVibrations: Boolean by kpref(key = "USE_VIBRATIONS", fallback = false)
    var useWifiADB: Boolean by kpref(key = "USE_WIFI_ADB", fallback = false)
    var versionCode: Int by kpref(key = "VERSION_CODE", fallback = -1)
    var viewpagerSwipe: Boolean by kpref(key = "VIEWPAGER_SWIPE", fallback = false)

    object Appearance {
        var themeIndex: Int by kpref(
            key = "THEME_INDEX",
            fallback = Theme.LIGHT.ordinal,
            postSetter = { value: Int ->
                themeLoader.invalidate()
                logAnalytics(name = "Theme", events = *arrayOf("Count" to Theme(value).name))
            }
        )
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

        var customTextColor: Int by kpref(key = "CUSTOM_COLOR_TEXT", fallback = Theme.PORCELAIN)
        var customAccentColor: Int by kpref(key = "CUSTOM_COLOR_ACCENT", fallback = Theme.LOCHMARA)
        var customBackgroundColor: Int by kpref(
            key = "CUSTOM_COLOR_BACKGROUND",
            fallback = Theme.MINE_SHAFT
        )
        var customHeaderColor: Int by kpref(
            key = "CUSTOM_COLOR_HEADER",
            fallback = Theme.BAHAMA_BLUE
        )
        var customIconColor: Int by kpref(key = "CUSTOM_COLOR_ICONS", fallback = Theme.PORCELAIN)

        var mainActivityLayoutIndex: Int by kpref(
            key = "MAIN_ACTIVITY_LAYOUT_INDEX",
            fallback = MainActivityLayout.TOP_BAR.ordinal,
            postSetter = { value: Int ->
                mainActivityLayoutLoader.invalidate()
                logAnalytics(
                    name = "Main Layout",
                    events = *arrayOf("Type" to MainActivityLayout(value).name)
                )
            }
        )
        private val mainActivityLayoutLoader =
            lazyResettable { MainActivityLayout.values()[mainActivityLayoutIndex] }
        val mainActivityLayout: MainActivityLayout by mainActivityLayoutLoader

        var dateTimeFormatIndex: Int by kpref(
            key = "DATE_TIME_FORMAT_INDEX",
            fallback = DateTimeFormat.MEDIUM_DATE_MEDIUM_TIME.ordinal,
            postSetter = { value: Int ->
                dateTimeFormatLoader.invalidate()
                logAnalytics(
                    name = "Date time format",
                    events = *arrayOf("Date time format" to DateTimeFormat(value).name)
                )
            }
        )
        private val dateTimeFormatLoader =
            lazyResettable { DateTimeFormat.values()[dateTimeFormatIndex] }
        val dateTimeFormat: DateTimeFormat by dateTimeFormatLoader
    }

    // Map layers
    var isTrafficEnabled: Boolean by kpref(key = "IS_TRAFFIC_ENABLED", fallback = false)
    var isBuildingsEnabled: Boolean by kpref(key = "IS_BUILDINGS_ENABLED", fallback = false)
    var isIndoorEnabled: Boolean by kpref(key = "IS_INDOOR_ENABLED", fallback = false)

    // Map UI
    var isCompassEnabled: Boolean by kpref("IS_COMPASS_ENABLED", fallback = true)
    var isMyLocationButtonEnabled: Boolean by kpref(
        "IS_MY_LOCATION_BUTTON_ENABLED",
        fallback = true
    )
    var isIndoorLevelPickerEnabled: Boolean by kpref(
        "IS_INDOOR_LEVEL_PICKER_ENABLED",
        fallback = false
    )

    // Map UX
    var isZoomGesturesEnabled: Boolean by kpref("IS_ZOOM_GESTURES_ENABLED", fallback = true)
    var isScrollGesturesEnabled: Boolean by kpref("IS_SCROLL_GESTURES_ENABLED", fallback = true)
    var isTiltGesturesEnabled: Boolean by kpref("IS_TILT_GESTURES_ENABLED", fallback = true)
    var isRotateGesturesEnabled: Boolean by kpref("IS_ROTATE_GESTURES_ENABLED", fallback = true)
}
