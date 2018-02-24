package de.uni_marburg.mathematik.ds.serval.enums

import android.support.annotation.StringRes
import com.google.android.gms.location.LocationRequest
import de.uni_marburg.mathematik.ds.serval.R

/**
 * Location request priorities.
 *
 * @property titleRes Resource ID of the title
 * @property descTextRes Resource ID of the description
 */
enum class LocationRequestPriority(
    @StringRes val titleRes: Int,
    @StringRes val descTextRes: Int,
    private val priorityGetter: () -> Int
) {

    /**
     * Use this setting to request the most precise location possible. With this setting,
     * the location services are more likely to use GPS to determine the location.
     */
    ACCURATE(
        titleRes = R.string.location_request_priority_accurate,
        descTextRes = R.string.location_request_priority_accurate_desc,
        priorityGetter = LocationRequest::PRIORITY_HIGH_ACCURACY
    ),

    /**
     * Use this setting to request location precision to within a city block, which is an accuracy
     * of approximately 100 meters. This is considered a coarse level of accuracy, and is likely to
     * consume less power. With this setting, the location services are likely to use WiFi and cell
     * tower positioning. Note, however, that the choice of location provider depends on many other
     * factors, such as which sources are available.
     */
    BLOCK_ACCURACY(
        titleRes = R.string.location_request_priority_block_accuracy,
        descTextRes = R.string.location_request_priority_block_accuracy_desc,
        priorityGetter = LocationRequest::PRIORITY_BALANCED_POWER_ACCURACY
    ),

    /**
     * Use this setting to request city-level precision, which is an accuracy of approximately
     * 10 kilometers. This is considered a coarse level of accuracy, and is likely to consume
     * less power.
     */
    CITY_ACCURACY(
        titleRes = R.string.location_request_priority_city_accuracy,
        descTextRes = R.string.location_request_priority_city_accuracy_desc,
        priorityGetter = LocationRequest::PRIORITY_LOW_POWER
    ),

    /**
     * Use this setting if you need negligible impact on power consumption, but want to receive
     * location updates when available. With this setting, your app does not trigger any location
     * updates, but receives locations triggered by other apps.
     */
    PASSIVE(
        titleRes = R.string.location_request_priority_passive,
        descTextRes = R.string.location_request_priority_passive_desc,
        priorityGetter = LocationRequest::PRIORITY_NO_POWER
    );

    val priority: Int
        get() = priorityGetter()

    companion object {
        operator fun invoke(index: Int) = values()[index]
    }
}