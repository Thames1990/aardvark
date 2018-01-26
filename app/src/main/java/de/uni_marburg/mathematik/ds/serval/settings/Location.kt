package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestPriority
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

fun SettingsActivity.getLocationPrefs(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.location)
    plainText(R.string.location_dependency_warning)

    // TODO Fix min/max dependency

    // Location request priority interval
    seekbar(
        title = R.string.location_request_interval,
        getter = Prefs::locationRequestInterval,
        setter = { Prefs.locationRequestInterval = it }
    ) {
        descRes = R.string.location_request_interval_description
        min = Prefs.locationRequestFastestInterval
    }

    // Location request priority fastest interval
    seekbar(
        title = R.string.location_request_fastest_interval,
        getter = Prefs::locationRequestFastestInterval,
        setter = { Prefs.locationRequestFastestInterval = it }
    ) {
        descRes = R.string.location_request_fastest_interval_description
        min = 1
        max = Prefs.locationRequestInterval
    }

    // Location request priority
    text(
        title = R.string.location_request_priority,
        getter = Prefs::locationRequestPriorityType,
        setter = { Prefs.locationRequestPriorityType = it }
    ) {
        onClick = {
            materialDialogThemed {
                title(R.string.location_request_priority)
                items(LocationRequestPriority.values()
                    .map { "${string(it.titleRes)}\n${string(it.descTextRes)}" }
                )
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
        textGetter = { string(LocationRequestPriority(it).titleRes) }
    }
}