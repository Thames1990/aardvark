package de.uni_marburg.mathematik.ds.serval.util

import android.location.Location

import java.util.concurrent.TimeUnit

/** A utility class for [locations][Location]. */
object LocationUtil {

    private val TWO_MINUTES = TimeUnit.MINUTES.toMillis(2)

    /** Determines whether a [location] is better than the [currentLocation]. */
    fun isBetterLocation(location: Location, currentLocation: Location?): Boolean {
        if (currentLocation == null) {
            return true
        }

        val timeDelta = location.time - currentLocation.time

        val isSignificantlyNewer = timeDelta > TWO_MINUTES
        val isSignificantlyOlder = timeDelta < -TWO_MINUTES
        val isNewer = timeDelta > 0

        if (isSignificantlyNewer) {
            return true
        } else if (isSignificantlyOlder) {
            return false
        }

        val accuracyDelta = (location.accuracy - currentLocation.accuracy).toInt()

        val isLessAccurate = accuracyDelta > 0
        val isMoreAccurate = accuracyDelta < 0
        val isSignificantlyLessAccurate = accuracyDelta > 200
        val isFromSameProvider = isSameProvider(location.provider, currentLocation.provider)

        if (isMoreAccurate) {
            return true
        } else if (isNewer && !isLessAccurate) {
            return true
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true
        }
        return false
    }

    /** Determines whether [provider1] is from the same type as [provider2]. */
    private fun isSameProvider(provider1: String?, provider2: String?): Boolean =
            if (provider1 == null) {
                provider2 == null
            } else provider1 == provider2


}
