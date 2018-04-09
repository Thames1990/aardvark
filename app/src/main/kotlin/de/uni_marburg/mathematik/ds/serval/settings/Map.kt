package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefItemBase
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.toast
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.MapStyles
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import de.uni_marburg.mathematik.ds.serval.utils.logAnalytics
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

object MapPrefs : KPref() {

    const val MAP_PADDING = 160
    const val MAP_ZOOM = 15f

    var compassEnabled: Boolean by kpref(key = "COMPASS_ENABLED", fallback = true)
    var indoorLevelPickerEnabled: Boolean by kpref(
        key = "INDOOR_LEVEL_PICKER_ENABLED",
        fallback = false
    )
    var myLocationButtonEnabled: Boolean by kpref(
        key = "MY_LOCATION_BUTTON_ENABLED",
        fallback = true
    )
    var showExactClusterSize: Boolean by kpref(key = "SHOW_EXACT_CLUSTER_SIZE", fallback = false)

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
            postSetter = {
                loader.invalidate()
                logAnalytics(
                    name = "Maps style",
                    events = *arrayOf("Count" to MapStyles(it).name)
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

    header(title = R.string.preference_map_layers_header)

    checkbox(
        title = R.string.preference_map_is_traffic_enabled,
        getter = MapPrefs.Layers::trafficEnabled,
        setter = { trafficEnabled ->
            MapPrefs.Layers.trafficEnabled = trafficEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_traffic_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_buildings_enabled,
        getter = MapPrefs.Layers::buildingsEnabled,
        setter = { buildingsEnabled ->
            MapPrefs.Layers.buildingsEnabled = buildingsEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_buildings_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_enabled,
        getter = MapPrefs.Layers::indoorEnabled,
        setter = { indoorEnabled ->
            MapPrefs.Layers.indoorEnabled = indoorEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_enabled_desc }
    )

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = AppearancePrefs.Theme::isCustomTheme
        onDisabledClick = { toast(R.string.preference_requires_custom_theme) }
    }

    header(title = R.string.preference_map_ui_header)

    checkbox(
        title = R.string.preference_map_is_compass_enabled,
        getter = MapPrefs::compassEnabled,
        setter = { compassEnabled ->
            MapPrefs.compassEnabled = compassEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_compass_enabled_desc }
    )

    fun KPrefItemBase.BaseContract<Boolean>.dependsOnLocationPermission() {
        enabler = ::hasLocationPermission
        onDisabledClick = { toast(R.string.preference_location_requires_location_permission) }
    }

    checkbox(
        title = R.string.preference_map_is_my_location_button_enabled,
        getter = MapPrefs::myLocationButtonEnabled,
        setter = { myLocationButtonEnabled ->
            MapPrefs.myLocationButtonEnabled = myLocationButtonEnabled
            shouldRestartMain()
        },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.preference_map_is_my_location_button_enabled_desc
        }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_level_picker_enabled,
        getter = MapPrefs::indoorLevelPickerEnabled,
        setter = { indoorLevelPickerEnabled ->
            MapPrefs.indoorLevelPickerEnabled = indoorLevelPickerEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_level_picker_enabled_desc }
    )

    fun showMapStyleChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_map_style)
            items(MapStyles.values().map { mapsStyle -> string(mapsStyle.titleRes) })
            with(onClick) {
                itemsCallbackSingleChoice(item.pref) { _, _, which, _ ->
                    if (item.pref != which) {
                        item.pref = which
                        shouldRestartMain()
                        reload()
                    }
                    true
                }
            }
        }
    }

    checkbox(
        title = R.string.preference_map_show_exact_cluster_size,
        getter = MapPrefs::showExactClusterSize,
        setter = { showExactClusterSize ->
            MapPrefs.showExactClusterSize = showExactClusterSize
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_show_exact_cluster_size_desc }
    )

    text(
        title = R.string.preference_map_style,
        getter = MapPrefs.MapStyle::index,
        setter = { MapPrefs.MapStyle.index = it },
        builder = {
            dependsOnCustom()
            onClick = ::showMapStyleChooserDialog
            textGetter = { string(MapStyles(it).titleRes) }
        }
    )

    header(title = R.string.preference_map_ux_header)

    checkbox(
        title = R.string.preference_map_is_zoom_gestures_enabled,
        getter = MapPrefs.Gestures::zoomEnabled,
        setter = { zoomEnabled ->
            MapPrefs.Gestures.zoomEnabled = zoomEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_zoom_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_scroll_gestures_enabled,
        getter = MapPrefs.Gestures::scrollEnabled,
        setter = { scrollEnabled ->
            MapPrefs.Gestures.scrollEnabled = scrollEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_scroll_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_tilt_gestures_enabled,
        getter = MapPrefs.Gestures::tiltEnabled,
        setter = { tiltEnabled ->
            MapPrefs.Gestures.tiltEnabled = tiltEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_tilt_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_rotate_gestures_enabled,
        getter = MapPrefs.Gestures::rotateEnabled,
        setter = { rotateEnabled ->
            MapPrefs.Gestures.rotateEnabled = rotateEnabled
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_rotate_gestures_enabled_desc }
    )

}