package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.MapStyle
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.setTheme
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

fun SettingsActivity.mapItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.preference_map_layers_header)

    checkbox(
        title = R.string.preference_map_is_traffic_enabled,
        getter = Prefs.Map::isTrafficEnabled,
        setter = {
            Prefs.Map.isTrafficEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_traffic_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_buildings_enabled,
        getter = Prefs.Map::isBuildingsEnabled,
        setter = {
            Prefs.Map.isBuildingsEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_buildings_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_enabled,
        getter = Prefs.Map::isIndoorEnabled,
        setter = {
            Prefs.Map.isIndoorEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_enabled_desc }
    )

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = Prefs.Appearance::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
    }

    header(R.string.preference_map_ui_header)

    checkbox(
        title = R.string.preference_map_is_compass_enabled,
        getter = Prefs.Map::isCompassEnabled,
        setter = {
            Prefs.Map.isCompassEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_compass_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_my_location_button_enabled,
        getter = Prefs.Map::isMyLocationButtonEnabled,
        setter = {
            Prefs.Map.isMyLocationButtonEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_my_location_button_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_level_picker_enabled,
        getter = Prefs.Map::isIndoorLevelPickerEnabled,
        setter = {
            Prefs.Map.isIndoorLevelPickerEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_level_picker_enabled_desc }
    )

    text(
        title = R.string.preference_map_style,
        getter = Prefs.Map::mapStyleIndex,
        setter = { Prefs.Map.mapStyleIndex = it },
        builder = {
            dependsOnCustom()
            onClick = {
                materialDialogThemed {
                    title(R.string.preference_map_style)
                    items(MapStyle.values().map { mapsStyle -> string(mapsStyle.titleRes) })
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
            textGetter = { string(MapStyle(it).titleRes) }
        }
    )

    header(R.string.preference_map_ux_header)

    checkbox(
        title = R.string.preference_map_is_zoom_gestures_enabled,
        getter = Prefs.Map::isZoomGesturesEnabled,
        setter = {
            Prefs.Map.isZoomGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_zoom_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_scroll_gestures_enabled,
        getter = Prefs.Map::isScrollGesturesEnabled,
        setter = {
            Prefs.Map.isScrollGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_scroll_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_tilt_gestures_enabled,
        getter = Prefs.Map::isTiltGesturesEnabled,
        setter = {
            Prefs.Map.isTiltGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_tilt_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_rotate_gestures_enabled,
        getter = Prefs.Map::isRotateGesturesEnabled,
        setter = {
            Prefs.Map.isRotateGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_rotate_gestures_enabled_desc }
    )

}