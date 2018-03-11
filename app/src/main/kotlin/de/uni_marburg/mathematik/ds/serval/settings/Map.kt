package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

fun SettingsActivity.mapItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.preference_map_layers)

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

}