package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.google.android.gms.location.LocationRequest
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Created by thames1990 on 07.01.18.
 */
enum class LocationRequestPriority(
        val priority: Int,
        @StringRes val textRes: Int,
        @StringRes val descTextRes: Int
) {
    PASSIVE(
            LocationRequest.PRIORITY_NO_POWER,
            R.string.location_request_priority_passive,
            R.string.location_request_priority_passive_description
    ),
    CITY_ACCURACY(
            LocationRequest.PRIORITY_LOW_POWER,
            R.string.location_request_priority_city_accuracy,
            R.string.location_request_priority_city_accuracy_description
    ),
    BLOCK_ACCURACY(
            LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY,
            R.string.location_request_priority_block_accuracy,
            R.string.location_request_priority_block_accuracy_description
    ),
    ACCURATE(
            LocationRequest.PRIORITY_HIGH_ACCURACY,
            R.string.location_request_priority_accurate,
            R.string.location_request_priority_accurate_description
    );

    companion object {
        val values = values() // save one instance
        operator fun invoke(index: Int) = values[index]
    }
}