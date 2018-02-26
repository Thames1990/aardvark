package de.uni_marburg.mathematik.ds.serval.model

import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import io.nlopez.smartlocation.SmartLocation
import io.nlopez.smartlocation.location.config.LocationParams
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesWithFallbackProvider

/**
 * Tracks changes of the location
 */
class LocationLiveData(context: Context) : LiveData<Location>() {

    private val locationParams: LocationParams = LocationParams.Builder()
        .setAccuracy(Prefs.locationRequestAccuracy.accuracy)
        .setDistance(Prefs.locationRequestDistance.toFloat())
        .setInterval(Prefs.locationRequestInterval.toLong())
        .build()

    private val locationControl = SmartLocation.with(context)
        .location(LocationGooglePlayServicesWithFallbackProvider(context))
        .config(locationParams)

    override fun onActive() {
        super.onActive()
        locationControl.start { location -> value = location }
    }

    override fun onInactive() {
        super.onInactive()
        locationControl.stop()
    }

}