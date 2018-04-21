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
import ca.allanwang.kau.utils.toast
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestPriorities
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

object LocationPrefs : KPref() {
    object LocationRequestPriority {
        var index: Int by kpref(
            key = "LOCATION_REQUEST_PRIORITY_INDEX",
            fallback = LocationRequestPriorities.HIGH_ACCURACY.ordinal,
            postSetter = { loader.invalidate() }
        )
        private val loader = lazyResettable { LocationRequestPriorities.values()[index] }
        private val locationRequestPriority: LocationRequestPriorities by loader
        val priority
            get() = locationRequestPriority.priority
    }

    var interval: Int by kpref(
        key = "LOCATION_REQUEST_INTERVAL",
        fallback = LocationRequestPriorities.MIN_INTERVAL
    )
    val intervalInMilliseconds: Long
        get() = (interval * 1000).toLong()

    var fastestInterval: Int by kpref(
        key = "LOCATION_REQUEST_FASTEST_INTERVAL",
        fallback = LocationRequestPriorities.MIN_FASTEST_INTERVAL
    )
    val fastestIntervalInMilliseconds: Long
        get() = (fastestInterval * 1000).toLong()

    var latitude: Float by kpref(key = "LATITUDE", fallback = 0.0)
    var longitude: Float by kpref(key = "LONGITUDE", fallback = 0.0)
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
        onDisabledClick = { toast(R.string.preference_location_requires_location_permission) }
    }

    fun showLocationRequestPriorityChooserDialog(onClick: KClick<Int>) {
        materialDialogThemed {
            title(R.string.preference_location_request_priority)
            items(LocationRequestPriorities.values().map { priority ->
                "${string(priority.titleRes)}\n${string(priority.descTextRes)}"
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
        title = R.string.preference_location_request_priority,
        getter = LocationPrefs.LocationRequestPriority::index,
        setter = { LocationPrefs.LocationRequestPriority.index = it },
        builder = {
            dependsOnLocationPermission()
            onClick = ::showLocationRequestPriorityChooserDialog
            textGetter = { string(LocationRequestPriorities(it).titleRes) }
        }
    )

    fun KPrefSeekbar.KPrefSeekbarContract.dependsOnLocationPermission() {
        enabler = ::hasLocationPermission
        onDisabledClick = { toast(R.string.preference_location_requires_location_permission) }
    }

    seekbar(
        title = R.string.preference_location_request_interval,
        getter = LocationPrefs::interval,
        setter = { LocationPrefs.interval = it },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.preference_location_request_interval_desc
            min = LocationRequestPriorities.MIN_INTERVAL
            max = LocationRequestPriorities.MAX_INTERVAL
        }
    )

    seekbar(
        title = R.string.preference_location_request_fastest_interval,
        getter = LocationPrefs::fastestInterval,
        setter = { LocationPrefs.fastestInterval = it },
        builder = {
            dependsOnLocationPermission()
            descRes = R.string.preference_location_request_fastest_interval_desc
            min = LocationRequestPriorities.MIN_FASTEST_INTERVAL
            max = LocationRequestPriorities.MAX_FASTEST_INTERVAL
        }
    )

}