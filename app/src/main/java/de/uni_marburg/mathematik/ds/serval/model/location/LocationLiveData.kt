package de.uni_marburg.mathematik.ds.serval.model.location

import android.Manifest
import android.annotation.SuppressLint
import android.arch.lifecycle.LiveData
import android.content.Context
import android.location.Location
import android.os.Looper
import ca.allanwang.kau.utils.hasPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import java.util.concurrent.TimeUnit

/** Tracks changes of the location of the current device */
class LocationLiveData(private val context: Context) : LiveData<Location>() {

    /** Location provider client */
    private val client: FusedLocationProviderClient by lazy { FusedLocationProviderClient(context) }

    /** Requests location updates */
    private val locationRequest: LocationRequest by lazy { LocationRequest() }

    /** Updates the last location of the device, if the position has changed */
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
        val hasLocationPermission = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasLocationPermission) {
            locationRequest.apply {
                interval = TimeUnit.SECONDS.toMillis(Prefs.locationRequestInterval.toLong())
                fastestInterval =
                        TimeUnit.SECONDS.toMillis(Prefs.locationRequestFastestInterval.toLong())
                priority = Prefs.priority
            }
            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    override fun onInactive() {
        super.onInactive()
        client.removeLocationUpdates(locationCallback)
    }
}