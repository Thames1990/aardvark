package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.google.android.gms.location.LocationRequest
import de.uni_marburg.mathematik.ds.serval.R

enum class LocationRequestPriority(
    val priority: Int,
    @StringRes val textRes: Int,
    @StringRes val descTextRes: Int
) {
    PASSIVE(
        priority = LocationRequest.PRIORITY_NO_POWER,
        textRes = R.string.location_request_priority_passive,
        descTextRes = R.string.location_request_priority_passive_description
    ),
    CITY_ACCURACY(
        priority = LocationRequest.PRIORITY_LOW_POWER,
        textRes = R.string.location_request_priority_city_accuracy,
        descTextRes = R.string.location_request_priority_city_accuracy_description
    ),
    BLOCK_ACCURACY(
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
        textRes = R.string.location_request_priority_block_accuracy,
        descTextRes = R.string.location_request_priority_block_accuracy_description
    ),
    ACCURATE(
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY,
        textRes = R.string.location_request_priority_accurate,
        descTextRes = R.string.location_request_priority_accurate_description
    );

    companion object {
        val values = values() // save one instance
        operator fun invoke(index: Int) = values[index]
    }
}