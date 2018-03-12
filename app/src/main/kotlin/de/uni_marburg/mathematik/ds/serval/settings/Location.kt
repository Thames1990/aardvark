package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kotlin.lazyResettable
import ca.allanwang.kau.kpref.KPref
import ca.allanwang.kau.kpref.activity.KClick
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefSeekbar
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.kpref.kpref
import ca.allanwang.kau.permissions.PERMISSION_ACCESS_FINE_LOCATION
import ca.allanwang.kau.permissions.kauRequestPermissions
import ca.allanwang.kau.utils.restartApplication
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestAccuracies
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestAccuracies.Companion.MAX_DISTANCE
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestAccuracies.Companion.MAX_INTERVAL
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestAccuracies.Companion.MIN_DISTANCE
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestAccuracies.Companion.MIN_INTERVAL
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed
import de.uni_marburg.mathematik.ds.serval.utils.snackbarThemed
import io.nlopez.smartlocation.location.config.LocationAccuracy
import kotlin.math.roundToInt

object LocationPrefs : KPref() {
    object LocationRequestAccuracy {
        var index: Int by kpref(
            key = "LOCATION_REQUEST_ACCURACY_INDEX",
            fallback = LocationRequestAccuracies.HIGH.ordinal,
            postSetter = { loader.invalidate() }
        )
        private val loader = lazyResettable { LocationRequestAccuracies.values()[index] }
        private val requestAccuracy: LocationRequestAccuracies by loader
        val accuracy: LocationAccuracy
            get() = requestAccuracy.accuracy
    }

    var interval: Int by kpref(
        key = "LOCATION_REQUEST_INTERVAL",
        fallback = arrayOf(
            LocationRequestAccuracies.MIN_INTERVAL,
            LocationRequestAccuracies.MAX_INTERVAL
        ).average().roundToInt()
    )
    var distance: Int by kpref(
        key = "LOCATION_REQUEST_DISTANCE",
        fallback = arrayOf(
            LocationRequestAccuracies.MIN_DISTANCE,
            LocationRequestAccuracies.MAX_DISTANCE
        ).average().roundToInt()
    )
}

fun SettingsActivity.locationItemBuilder(): KPrefAdapterBuilder.() -> Unit = {

    if (!hasLocationPermission) {
        plainText(
            title = R.string.preference_location_requires_location_permission,
            builder = {
                descRes = R.string.grant_location_permission_settings
                onClick = {
                    kauRequestPermissions(PERMISSION_ACCESS_FINE_LOCATION) { granted, _ ->
                        if (granted) restartApplication()
                    }
                }
            }
        )
    }

    fun KPrefText.KPrefTextContract<Int>.dependsOnLocationPermission() {
        enabler = ::hasLocationPermission
        onDisabledClick = {
            snackbarThemed(R.string.preference_location_requires_location_permission)
        }
    }

    fun showLocationRequestAccuracyChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_location_request_accuracy)
            items(LocationRequestAccuracies.values().map { accuracy ->
                "${string(accuracy.titleRes)}\n${string(accuracy.descTextRes)}"
            })
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

    text(
        title = R.string.preference_location_request_accuracy,
        getter = LocationPrefs.LocationRequestAccuracy::index,
        setter = { LocationPrefs.LocationRequestAccuracy.index = it },
        builder = {
            dependsOnLocationPermission()
            onClick = ::showLocationRequestAccuracyChooserDialog
            textGetter = { string(LocationRequestAccuracies(it).titleRes) }
        }
    )

    fun KPrefSeekbar.KPrefSeekbarContract.dependsOnLocationPermission() {
        enabler = ::hasLocationPermission
        onDisabledClick = {
            snackbarThemed(R.string.preference_location_requires_location_permission)
        }
    }

    seekbar(
        title = R.string.preference_location_request_distance,
        getter = LocationPrefs::distance,
        setter = { LocationPrefs.distance = it },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.preference_location_request_distance_desc
            min = MIN_DISTANCE
            max = MAX_DISTANCE
        }
    )

    seekbar(
        title = R.string.preference_location_request_interval,
        getter = LocationPrefs::interval,
        setter = { LocationPrefs.interval = it },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.preference_location_request_interval_desc
            min = MIN_INTERVAL
            max = MAX_INTERVAL
        }
    )

}