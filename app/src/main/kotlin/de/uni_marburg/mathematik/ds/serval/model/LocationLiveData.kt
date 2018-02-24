package de.uni_marburg.mathematik.ds.serval.model

import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import io.nlopez.smartlocation.SmartLocation

/**
 * Tracks changes of the location
 */
class LocationLiveData(context: Context) : LiveData<Location>() {

    private val locationControl = SmartLocation.with(context).location()

    override fun onActive() {
        super.onActive()
        locationControl.start { location -> value = location }
    }

    override fun onInactive() {
        super.onInactive()
        locationControl.stop()
    }

}