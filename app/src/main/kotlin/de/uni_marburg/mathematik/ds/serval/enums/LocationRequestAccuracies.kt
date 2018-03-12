package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import de.uni_marburg.mathematik.ds.serval.R
import io.nlopez.smartlocation.location.config.LocationAccuracy

/**
 * Location request accuracies.
 *
 * @property titleRes Resource ID of the title
 * @property descTextRes Resource ID of the description
 * @property accuracy Accuracy of a location request
 */
enum class LocationRequestAccuracies(
    @StringRes val titleRes: Int,
    @StringRes val descTextRes: Int,
    val accuracy: LocationAccuracy
) {

    HIGH(
        titleRes = R.string.location_request_accuracy_high,
        descTextRes = R.string.location_request_accuracy_high_desc,
        accuracy = LocationAccuracy.HIGH
    ),

    MEDIUM(
        titleRes = R.string.location_request_accuracy_medium,
        descTextRes = R.string.location_request_accuracy_medium_desc,
        accuracy = LocationAccuracy.MEDIUM
    ),

    LOW(
        titleRes = R.string.location_request_accuracy_low,
        descTextRes = R.string.location_request_accuracy_low_desc,
        accuracy = LocationAccuracy.LOW
    ),

    LOWEST(
        titleRes = R.string.location_request_accuracy_lowest,
        descTextRes = R.string.location_request_accuracy_lowest_desc,
        accuracy = LocationAccuracy.LOWEST
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]

        const val MIN_DISTANCE = 0
        const val MAX_DISTANCE = 500

        const val MIN_INTERVAL = 500
        const val MAX_INTERVAL = 5000
    }
}