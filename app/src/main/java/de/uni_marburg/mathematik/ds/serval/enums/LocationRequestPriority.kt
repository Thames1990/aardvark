package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.google.android.gms.location.LocationRequest
import de.uni_marburg.mathematik.ds.serval.R

enum class LocationRequestPriority(
    @StringRes val titleRes: Int,
    @StringRes val descTextRes: Int,
    val priorityGetter: () -> Int
) {
    ACCURATE(
        titleRes = R.string.location_request_priority_accurate,
        descTextRes = R.string.location_request_priority_accurate_description,
        priorityGetter = LocationRequest::PRIORITY_HIGH_ACCURACY
    ),

    BLOCK_ACCURACY(
        titleRes = R.string.location_request_priority_block_accuracy,
        descTextRes = R.string.location_request_priority_block_accuracy_description,
        priorityGetter = LocationRequest::PRIORITY_BALANCED_POWER_ACCURACY
    ),

    CITY_ACCURACY(
        titleRes = R.string.location_request_priority_city_accuracy,
        descTextRes = R.string.location_request_priority_city_accuracy_description,
        priorityGetter = LocationRequest::PRIORITY_LOW_POWER
    ),

    PASSIVE(
        titleRes = R.string.location_request_priority_passive,
        descTextRes = R.string.location_request_priority_passive_description,
        priorityGetter = LocationRequest::PRIORITY_NO_POWER
    );

    val priority: Int
        get() = priorityGetter()

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }
}