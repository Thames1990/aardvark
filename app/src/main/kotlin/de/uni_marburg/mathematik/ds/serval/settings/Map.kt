package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.MapStyles
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.setTheme
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

fun SettingsActivity.mapItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.preference_map_layers_header)

    checkbox(
        title = R.string.preference_map_is_traffic_enabled,
        getter = Prefs.Map.Layers::trafficEnabled,
        setter = {
            Prefs.Map.Layers.trafficEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_traffic_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_buildings_enabled,
        getter = Prefs.Map.Layers::buildingsEnabled,
        setter = {
            Prefs.Map.Layers.buildingsEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_buildings_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_enabled,
        getter = Prefs.Map.Layers::indoorEnabled,
        setter = {
            Prefs.Map.Layers.indoorEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_enabled_desc }
    )

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = Prefs.Appearance.Theme::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
    }

    header(R.string.preference_map_ui_header)

    checkbox(
        title = R.string.preference_map_is_compass_enabled,
        getter = Prefs.Map::compassEnabled,
        setter = {
            Prefs.Map.compassEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_compass_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_my_location_button_enabled,
        getter = Prefs.Map::myLocationButtonEnabled,
        setter = {
            Prefs.Map.myLocationButtonEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_my_location_button_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_indoor_level_picker_enabled,
        getter = Prefs.Map::indoorLevelPickerEnabled,
        setter = {
            Prefs.Map.indoorLevelPickerEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_indoor_level_picker_enabled_desc }
    )

    text(
        title = R.string.preference_map_style,
        getter = Prefs.Map.MapStyle::index,
        setter = { Prefs.Map.MapStyle.index = it },
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
        getter = Prefs.Map.Gestures::zoomEnabled,
        setter = {
            Prefs.Map.Gestures.zoomEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_zoom_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_scroll_gestures_enabled,
        getter = Prefs.Map.Gestures::scrollEnabled,
        setter = {
            Prefs.Map.Gestures.scrollEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_scroll_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_tilt_gestures_enabled,
        getter = Prefs.Map.Gestures::tiltEnabled,
        setter = {
            Prefs.Map.Gestures.tiltEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_tilt_gestures_enabled_desc }
    )

    checkbox(
        title = R.string.preference_map_is_rotate_gestures_enabled,
        getter = Prefs.Map.Gestures::rotateEnabled,
        setter = {
            Prefs.Map.Gestures.rotateEnabled = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_is_rotate_gestures_enabled_desc }
    )

}