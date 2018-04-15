package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.google.android.gms.location.LocationRequest
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Location request accuracies.
 *
 * @property titleRes Resource ID of the title
 * @property descTextRes Resource ID of the description
 * @property priority Priority of a location request
 */
enum class LocationRequestPriorities(
    @StringRes val titleRes: Int,
    @StringRes val descTextRes: Int,
    val priority: Int
) {

    HIGH_ACCURACY(
        titleRes = R.string.location_request_priority_high_accuracy,
        descTextRes = R.string.location_request_priority_high_accuracy_desc,
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    ),

    BALANCED_POWER_ACCURACY(
        titleRes = R.string.location_request_priority_balanced_power_accuracy,
        descTextRes = R.string.location_request_priority_balanced_power_accuracy_desc,
        priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
    ),

    LOW_POWER(
        titleRes = R.string.location_request_priority_low_power,
        descTextRes = R.string.location_request_priority_low_power_desc,
        priority = LocationRequest.PRIORITY_LOW_POWER
    ),

    NO_POWER(
        titleRes = R.string.location_request_priority_no_power,
        descTextRes = R.string.location_request_priority_no_power_desc,
        priority = LocationRequest.PRIORITY_NO_POWER
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]

        const val MIN_INTERVAL = 10
        const val MAX_INTERVAL = 3600

        const val MIN_FASTEST_INTERVAL = MIN_INTERVAL / 2
        const val MAX_FASTEST_INTERVAL = MAX_INTERVAL / 2
    }
}