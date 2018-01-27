package de.uni_marburg.mathematik.ds.serval.settings

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import ca.allanwang.kau.kpref.activity.items.KPrefSeekbar
import ca.allanwang.kau.kpref.activity.items.KPrefText
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.enums.LocationRequestPriority
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.aardvarkSnackbar
import de.uni_marburg.mathematik.ds.serval.utils.hasLocationPermission
import de.uni_marburg.mathematik.ds.serval.utils.materialDialogThemed

fun SettingsActivity.getLocationPrefs(): KPrefAdapterBuilder.() -> Unit = {

    fun openPermissionSettings() {
        val permissionIntent = Intent().apply {
            action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(permissionIntent)
        // TODO Wait for users decision and restart application if location permission was granted
    }

    if (!hasLocationPermission) {
        plainText(R.string.requires_location_permission) {
            descRes = R.string.grant_location_permission
            onClick = { openPermissionSettings() }
        }
    }

    fun KPrefText.KPrefTextContract<Int>.dependsOnLocationPermission() {
        enabler = { hasLocationPermission }
        onDisabledClick = { aardvarkSnackbar(R.string.requires_location_permission) }
    }

    fun KPrefSeekbar.KPrefSeekbarContract.dependsOnLocationPermission() {
        enabler = { hasLocationPermission }
        onDisabledClick = { aardvarkSnackbar(R.string.requires_location_permission) }
    }

    // Location request priority
    text(
        title = R.string.location_request_priority,
        getter = Prefs::locationRequestPriorityType,
        setter = { Prefs.locationRequestPriorityType = it }
    ) {
        dependsOnLocationPermission()
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

    // Location request priority interval
    seekbar(
        title = R.string.location_request_interval,
        getter = { Prefs.locationRequestInterval.toInt() },
        setter = { locationRequestInterval ->
            Prefs.locationRequestInterval = locationRequestInterval.toLong()
        }
    ) {
        dependsOnLocationPermission()
        descRes = R.string.location_request_interval_description
    }

    // Location request priority fastest interval
    seekbar(
        title = R.string.location_request_fastest_interval,
        getter = { Prefs.locationRequestFastestInterval.toInt() },
        setter = { locationRequestFastestInterval ->
            Prefs.locationRequestFastestInterval = locationRequestFastestInterval.toLong()
        }
    ) {
        dependsOnLocationPermission()
        descRes = R.string.location_request_fastest_interval_description
    }
}