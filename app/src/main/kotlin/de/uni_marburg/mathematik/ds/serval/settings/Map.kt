package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.MapStyles
import de.uni_marburg.mathematik.ds.serval.utils.logAnalytics
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.setTheme
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

object MapPrefs: KPref() {
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

fun SettingsActivity.mapItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.preference_map_layers_header)

    checkbox(
        title = R.string.preference_map_is_traffic_enabled,
        getter = MapPrefs.Layers::trafficEnabled,
        setter = {
            MapPrefs.Layers.trafficEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_traffic_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_buildings_enabled,
        getter = MapPrefs.Layers::buildingsEnabled,
        setter = {
            MapPrefs.Layers.buildingsEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_buildings_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_enabled,
        getter = MapPrefs.Layers::indoorEnabled,
        setter = {
            MapPrefs.Layers.indoorEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_enabled_desc }
    )

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = AppearancePrefs.Theme::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
    }

    header(R.string.preference_map_ui_header)

    checkbox(
        title = R.string.preference_map_is_compass_enabled,
        getter = MapPrefs::compassEnabled,
        setter = {
            MapPrefs.compassEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_compass_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_my_location_button_enabled,
        getter = MapPrefs::myLocationButtonEnabled,
        setter = {
            MapPrefs.myLocationButtonEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_my_location_button_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_level_picker_enabled,
        getter = MapPrefs::indoorLevelPickerEnabled,
        setter = {
            MapPrefs.indoorLevelPickerEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_level_picker_enabled_desc }
    )

    text(
        title = R.string.preference_map_style,
        getter = MapPrefs.MapStyle::index,
        setter = { MapPrefs.MapStyle.index = it },
        builder = {
            dependsOnCustom()
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_map_style)
                    items(MapStyles.values().map { mapsStyle -> string(mapsStyle.titleRes) })
                    itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                        if (item.pref != which) {
                            item.pref = which
                            shouldRestartMain()
                            reload()
                            setTheme()
                            themeExterior()
                            invalidateOptionsMenu()
                        }
                        true
                    }
                }
            }
            textGetter = { string(MapStyles(it).titleRes) }
        }
    )

    header(R.string.preference_map_ux_header)

    checkbox(
        title = R.string.preference_map_is_zoom_gestures_enabled,
        getter = MapPrefs.Gestures::zoomEnabled,
        setter = {
            MapPrefs.Gestures.zoomEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_zoom_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_scroll_gestures_enabled,
        getter = MapPrefs.Gestures::scrollEnabled,
        setter = {
            MapPrefs.Gestures.scrollEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_scroll_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_tilt_gestures_enabled,
        getter = MapPrefs.Gestures::tiltEnabled,
        setter = {
            MapPrefs.Gestures.tiltEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_tilt_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_rotate_gestures_enabled,
        getter = MapPrefs.Gestures::rotateEnabled,
        setter = {
            MapPrefs.Gestures.rotateEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_rotate_gestures_enabled_desc }
    )

}