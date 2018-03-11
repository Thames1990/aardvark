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
        getter = Prefs::isTrafficEnabled,
        setter = {
            Prefs.isTrafficEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_traffic_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_buildings_enabled,
        getter = Prefs::isBuildingsEnabled,
        setter = {
            Prefs.isBuildingsEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_buildings_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_enabled,
        getter = Prefs::isIndoorEnabled,
        setter = {
            Prefs.isIndoorEnabled = it
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
        getter = Prefs::isCompassEnabled,
        setter = {
            Prefs.isCompassEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_compass_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_my_location_button_enabled,
        getter = Prefs::isMyLocationButtonEnabled,
        setter = {
            Prefs.isMyLocationButtonEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_my_location_button_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_level_picker_enabled,
        getter = Prefs::isIndoorLevelPickerEnabled,
        setter = {
            Prefs.isIndoorLevelPickerEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_level_picker_enabled_desc }
    )

    text(
        title = R.string.preference_map_style,
        getter = Prefs::mapStyleIndex,
        setter = { Prefs.mapStyleIndex = it },
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
        getter = Prefs::isZoomGesturesEnabled,
        setter = {
            Prefs.isZoomGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_zoom_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_scroll_gestures_enabled,
        getter = Prefs::isScrollGesturesEnabled,
        setter = {
            Prefs.isScrollGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_scroll_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_tilt_gestures_enabled,
        getter = Prefs::isTiltGesturesEnabled,
        setter = {
            Prefs.isTiltGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_tilt_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_rotate_gestures_enabled,
        getter = Prefs::isRotateGesturesEnabled,
        setter = {
            Prefs.isRotateGesturesEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_rotate_gestures_enabled_desc }
    )

}