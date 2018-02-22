package de.uni_marburg.mathematik.ds.serval.model.location

import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Looper
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import java.util.concurrent.TimeUnit

/**
 * Tracks changes of the location of the current device
 */
class LocationLiveData(context: Context) : LiveData<Location>() {

    /**
     * Location provider client
     */
    private val client: FusedLocationProviderClient by lazy { FusedLocationProviderClient(context) }

    /**
     * Requests location updates
     */
    private val locationRequest: LocationRequest by lazy { LocationRequest() }

    /**
     * Updates the last location of the device, if the position has changed
     */
    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                value = locationResult.lastLocation
            }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        locationRequest.apply {
            interval = TimeUnit.SECONDS.toMillis(Prefs.locationRequestInterval)
            fastestInterval = TimeUnit.SECONDS.toMillis(Prefs.locationRequestFastestInterval)
            priority = Prefs.locationRequestPriority.priority
        }
        client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
    }

    override fun onInactive() {
        super.onInactive()
        client.removeLocationUpdates(locationCallback)
    }
}