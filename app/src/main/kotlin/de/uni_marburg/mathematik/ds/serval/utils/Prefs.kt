package de.uni_marburg.mathematik.ds.serval.utils

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import de.uni_marburg.mathematik.ds.serval.enums.*
import io.nlopez.smartlocation.location.config.LocationAccuracy
import kotlin.math.roundToInt

object Prefs : KPref() {

    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var versionCode: Int by kpref(key = "VERSION_CODE", fallback = -1)

    object Appearance {
        object Theme {
            var index: Int by kpref(
                key = "THEME_INDEX",
                fallback = Themes.LIGHT.ordinal,
                postSetter = { value: Int ->
                    loader.invalidate()
                    logAnalytics(name = "Theme", events = *arrayOf("Count" to Themes(value).name))
                }
            )
            private val loader = lazyResettable { Themes.values()[index] }
            val theme: Themes by loader

            val accentColor: Int
                get() = theme.accentColor
            val backgroundColor: Int
                get() = theme.backgroundColor
            val headerColor: Int
                get() = theme.headerColor
            val iconColor: Int
                get() = theme.iconColor
            val isCustomTheme: Boolean
                get() = theme == Themes.CUSTOM
            val textColor: Int
                get() = theme.textColor

            var customTextColor: Int by kpref(
                key = "CUSTOM_COLOR_TEXT",
                fallback = Themes.PORCELAIN
            )
            var customAccentColor: Int by kpref(
                key = "CUSTOM_COLOR_ACCENT",
                fallback = Themes.LOCHMARA
            )
            var customBackgroundColor: Int by kpref(
                key = "CUSTOM_COLOR_BACKGROUND",
                fallback = Themes.MINE_SHAFT
            )
            var customHeaderColor: Int by kpref(
                key = "CUSTOM_COLOR_HEADER",
                fallback = Themes.BAHAMA_BLUE
            )
            var customIconColor: Int by kpref(
                key = "CUSTOM_COLOR_ICONS",
                fallback = Themes.PORCELAIN
            )
        }

        object DateTimeFormat {
            var index: Int by kpref(
                key = "DATE_TIME_FORMAT_INDEX",
                fallback = DateTimeFormats.MEDIUM_DATE_MEDIUM_TIME.ordinal,
                postSetter = { value: Int ->
                    loader.invalidate()
                    logAnalytics(
                        name = "Date time format",
                        events = *arrayOf("Date time format" to DateTimeFormats(value).name)
                    )
                }
            )
            private val loader = lazyResettable { DateTimeFormats.values()[index] }
            val format: DateTimeFormats by loader
        }

        object MainActivityLayout {
            var index: Int by kpref(
                key = "MAIN_ACTIVITY_LAYOUT_INDEX",
                fallback = MainActivityLayouts.TOP_BAR.ordinal,
                postSetter = { value: Int ->
                    loader.invalidate()
                    logAnalytics(
                        name = "Main Layout",
                        events = *arrayOf("Type" to MainActivityLayouts(value).name)
                    )
                }
            )
            private val loader = lazyResettable { MainActivityLayouts.values()[index] }
            val layout: MainActivityLayouts by loader

            val backgroundColor: Int
                get() = layout.backgroundColor
            val iconColor: Int
                get() = layout.iconColor
            val layoutRes: Int
                get() = layout.layoutRes
            val titleRes: Int
                get() = layout.titleRes
        }

        var tintNavBar: Boolean by kpref(key = "TINT_NAV_BAR", fallback = false)
    }

    object Behaviour {
        var analyticsEnabled: Boolean by kpref(key = "ANALYTICS_ENABLED", fallback = isReleaseBuild)
        var animationsEnabled: Boolean by kpref(
            key = "ANIMATIONS_ENABLED",
            fallback = true,
            postSetter = { value: Boolean ->
                logAnalytics(
                    name = "Animations enabled",
                    events = *arrayOf("Animations" to value)
                )
            }
        )
        var confirmExit: Boolean by kpref(key = "CONFIRM_EXIT", fallback = isReleaseBuild)
        var showChangelog: Boolean by kpref(key = "SHOW_CHANGELOG", fallback = isReleaseBuild)
    }

    object Experimental {
        var enabled: Boolean by kpref(
            key = "EXPERIMENTAL_SETTINGS_ENABLED",
            fallback = isDebugBuild
        )
        var secureApp: Boolean by kpref(key = "SECURE_APP", fallback = false)
        var showDownloadProgress: Boolean by kpref(
            key = "SHOW_DOWNLOAD_PROGRESS",
            fallback = false
        )
        var wifiADBEnabled: Boolean by kpref(key = "WIFI_ADB_ENABLED", fallback = false)
        var vibrationsEnabled: Boolean by kpref(key = "VIBRATIONS_ENABLED", fallback = false)
        var viewpagerSwipeEnabled: Boolean by kpref(
            key = "VIEWPAGER_SWIPE_ENABLED",
            fallback = false
        )
    }

    object Location {
        object RequestAccuracy {
            var index: Int by kpref(
                key = "LOCATION_REQUEST_ACCURACY_INDEX",
                fallback = LocationRequestAccuracies.HIGH.ordinal,
                postSetter = { loader.invalidate() }
            )
            private val loader = lazyResettable { LocationRequestAccuracies.values()[index] }
            private val requestAccuracy: LocationRequestAccuracies by loader
            val accuracy: LocationAccuracy
                get() = requestAccuracy.accuracy

            var interval: Int by kpref(
                key = "LOCATION_REQUEST_INTERVAL",
                fallback = arrayOf(
                    LocationRequestAccuracies.MIN_INTERVAL,
                    LocationRequestAccuracies.MAX_INTERVAL
                ).average().roundToInt()
            )
            var distance: Int by kpref(
                key = "LOCATION_REQUEST_DISTANCE",
                fallback = arrayOf(
                    LocationRequestAccuracies.MIN_DISTANCE,
                    LocationRequestAccuracies.MAX_DISTANCE
                ).average().roundToInt()
            )
        }
    }

    object Map {
        var compassEnabled: Boolean by kpref(key = "COMPASS_ENABLED", fallback = true)
        var indoorLevelPickerEnabled: Boolean by kpref(
            key = "INDOOR_LEVEL_PICKER_ENABLED",
            fallback = false
        )
        var myLocationButtonEnabled: Boolean by kpref(
            key = "MY_LOCATION_BUTTON_ENABLED",
            fallback = true
        )

        object Gestures {
            var rotateEnabled: Boolean by kpref(key = "ROTATE_GESTURES_ENABLED", fallback = true)
            var scrollEnabled: Boolean by kpref(key = "SCROLL_GESTURES_ENABLED", fallback = true)
            var tiltEnabled: Boolean by kpref(key = "TILT_GESTURES_ENABLED", fallback = true)
            var zoomEnabled: Boolean by kpref(key = "ZOOM_GESTURES_ENABLED", fallback = true)
        }

        object Layers {
            var buildingsEnabled: Boolean by kpref(key = "BUILDINGS_ENABLED", fallback = false)
            var indoorEnabled: Boolean by kpref(key = "INDOOR_ENABLED", fallback = false)
            var trafficEnabled: Boolean by kpref(key = "TRAFFIC_ENABLED", fallback = false)
        }

        object MapStyle {
            var index: Int by kpref(
                key = "MAPS_STYLE_INDEX",
                fallback = MapStyles.STANDARD.ordinal,
                postSetter = { value: Int ->
                    loader.invalidate()
                    logAnalytics(
                        name = "Maps style",
                        events = *arrayOf("Count" to MapStyles(value).name)
                    )
                }
            )
            private val loader = lazyResettable { MapStyles.values()[index] }
            private val mapStyle: MapStyles by loader
            val styleRes: Int
                get() = mapStyle.styleRes
        }
    }

    object Serval {
        const val EVENT_COUNT = 10000

        var baseUrl: String by kpref(key = "SERVAL_BASE_URL", fallback = "serval.splork.de")
        var password: String by kpref(key = "SERVAL_PASSWORD", fallback = "pum123")
        var port: Int by kpref(key = "SERVAL_PORT", fallback = 80)
        var user: String by kpref(key = "SERVAL_USER", fallback = "pum")

        var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    }

}
