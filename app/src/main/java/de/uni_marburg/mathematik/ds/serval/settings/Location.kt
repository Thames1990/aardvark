package de.uni_marburg.mathematik.ds.serval.settings

import ca.allanwang.kau.kpref.activity.KPrefAdapterBuilder
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.SettingsActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

/**
 * Created by thames1990 on 07.01.18.
 */
fun SettingsActivity.getLocationPrefs(): KPrefAdapterBuilder.() -> Unit = {

    header(R.string.location)

    plainText(R.string.location_dependency_warning)

    // TODO Fix min/max dependency

    seekbar(
            R.string.location_request_interval,
            { Prefs.locationRequestInterval },
            { Prefs.locationRequestInterval = it }
    ) {
        descRes = R.string.location_request_interval_description
        min = Prefs.locationRequestFastestInterval
    }

    seekbar(
            R.string.location_request_fastest_interval,
            { Prefs.locationRequestFastestInterval },
            { Prefs.locationRequestFastestInterval = it }
    ) {
        descRes = R.string.location_request_fastest_interval_description
        min = 1
        max = Prefs.locationRequestInterval
    }
}