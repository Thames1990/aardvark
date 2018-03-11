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
        title = R.string.preference_map_traffic,
        getter = Prefs::mapShowTraffic,
        setter = {
            Prefs.mapShowTraffic = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_traffic_desc }
    )

    checkbox(
        title = R.string.preference_map_buildings,
        getter = Prefs::mapShowBuildings,
        setter = {
            Prefs.mapShowBuildings = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_buildings_desc }
    )

    checkbox(
        title = R.string.preference_map_indoor,
        getter = Prefs::mapShowIndoor,
        setter = {
            Prefs.mapShowIndoor = it
            shouldRestartMain()
        },
        builder = { descRes = R.string.preference_map_indoor_desc }
    )

    fun KPrefText.KPrefTextContract<Int>.dependsOnCustom() {
        enabler = Prefs::isCustomTheme
        onDisabledClick = { snackbarThemed(R.string.preference_requires_custom_theme) }
    }

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

}