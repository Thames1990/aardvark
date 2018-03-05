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
enum class LocationRequestAccuracy(
    @StringRes val titleRes: Int,
    @StringRes val descTextRes: Int,
    val accuracy: LocationAccuracy
) {

    /**
     * Use this setting to request the most precise location possible. With this setting,
     * the location services are more likely to use GPS to determine the location.
     */
    HIGH(
        titleRes = R.string.location_request_priority_accurate,
        descTextRes = R.string.location_request_priority_accurate_desc,
        accuracy = LocationAccuracy.HIGH
    ),

    /**
     * Use this setting to request location precision to within a city block, which is an accuracy
     * of approximately 100 meters. This is considered a coarse level of accuracy, and is likely to
     * consume less power. With this setting, the location services are likely to use WiFi and cell
     * tower positioning. Note, however, that the choice of location provider depends on many other
     * factors, such as which sources are available.
     */
    MEDIUM(
        titleRes = R.string.location_request_priority_block_accuracy,
        descTextRes = R.string.location_request_priority_block_accuracy_desc,
        accuracy = LocationAccuracy.MEDIUM
    ),

    /**
     * Use this setting to request city-level precision, which is an accuracy of approximately
     * 10 kilometers. This is considered a coarse level of accuracy, and is likely to consume
     * less power.
     */
    LOW(
        titleRes = R.string.location_request_priority_city_accuracy,
        descTextRes = R.string.location_request_priority_city_accuracy_desc,
        accuracy = LocationAccuracy.LOW
    ),

    /**
     * Use this setting if you need negligible impact on power consumption, but want to receive
     * location updates when available. With this setting, your app does not trigger any location
     * updates, but receives locations triggered by other apps.
     */
    LOWEST(
        titleRes = R.string.location_request_priority_passive,
        descTextRes = R.string.location_request_priority_passive_desc,
        accuracy = LocationAccuracy.LOWEST
    );

    companion object {
        operator fun invoke(index: Int) = values()[index]

        const val LOCATION_REQUEST_MIN_DISTANCE = 0
        const val LOCATION_REQUEST_MAX_DISTANCE = 500

        const val LOCATION_REQUEST_MIN_INTERVAL = 500
        const val LOCATION_REQUEST_MAX_INTERVAL = 5000
    }
}