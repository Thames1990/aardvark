package de.uni_marburg.mathematik.ds.serval.utils

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.kpref
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.enums.MapStyles

// TODO Separate into different shared preferences
object Prefs : KPref() {

    var installDate: Long by kpref(key = "INSTALL_DATE", fallback = -1L)
    var lastLaunch: Long by kpref(key = "LAST_LAUNCH", fallback = -1L)
    var versionCode: Int by kpref(key = "VERSION_CODE", fallback = -1)

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

        var baseUrl: String by kpref(
            key = "SERVAL_BASE_URL",
            fallback = BuildConfig.SERVAL_BASE_URL
        )
        var password: String by kpref(
            key = "SERVAL_PASSWORD",
            fallback = BuildConfig.SERVAL_PASSWORD
        )
        var port: Int by kpref(key = "SERVAL_PORT", fallback = BuildConfig.SERVAL_PORT)
        var user: String by kpref(key = "SERVAL_USER", fallback = BuildConfig.SERVAL_USER)

        var eventCount: Int by kpref(key = "EVENT_COUNT", fallback = EVENT_COUNT)
    }

}
