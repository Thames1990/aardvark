package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefSeekbar
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.restartApplication
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestAccuracy
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed

fun SettingsActivity.locationItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    if (!hasLocationPermission) {
        plainText(R.string.preference_requires_location_permission) {
            descRes = R.string.grant_location_permission_settings
            onClick = {
                kauRequestPermissions(PERMISSION_ACCESS_FINE_LOCATION) { granted, _ ->
                    if (granted) restartApplication()
                }
            }
        }
    }

    fun KPrefText.KPrefTextContract<Int>.dependsOnLocationPermission() {
        enabler = ::hasLocationPermission
        onDisabledClick = { snackbarThemed(R.string.preference_requires_location_permission) }
    }

    fun KPrefSeekbar.KPrefSeekbarContract.dependsOnLocationPermission() {
        enabler = ::hasLocationPermission
        onDisabledClick = { snackbarThemed(R.string.preference_requires_location_permission) }
    }

    // Location request accuracy
    text(
        title = R.string.location_request_priority,
        getter = Prefs::locationRequestAccuracyIndex,
        setter = { Prefs.locationRequestAccuracyIndex = it },
        builder = {
            dependsOnLocationPermission()
            onClick = {
                materialDialogThemed {
                    title(R.string.location_request_priority)
                    items(LocationRequestAccuracy.values().map { priority ->
                        "${string(priority.titleRes)}\n${string(priority.descTextRes)}"
                    })
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
            textGetter = { string(LocationRequestAccuracy(it).titleRes) }
        }
    )

    // Location request distance
    seekbar(
        title = R.string.location_request_distance,
        getter = Prefs::locationRequestDistance,
        setter = { Prefs.locationRequestDistance = it },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.location_request_distance_desc
            min = Prefs.LOCATION_REQUEST_MIN_DISTANCE
            max = Prefs.LOCATION_REQUEST_MAX_DISTANCE
        }
    )

    // Location request interval
    seekbar(
        title = R.string.location_request_interval,
        getter = Prefs::locationRequestInterval,
        setter = { Prefs.locationRequestInterval = it },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.location_request_interval_desc
            min = Prefs.LOCATION_REQUEST_MIN_INTERVAL
            max = Prefs.LOCATION_REQUEST_MAX_INTERVAL
        }
    )

}