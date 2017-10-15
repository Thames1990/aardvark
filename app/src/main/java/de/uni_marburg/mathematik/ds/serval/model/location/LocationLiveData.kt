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
import java.util.concurrent.TimeUnit

class LocationLiveData(val context: Context) : LiveData<Location>() {

    private val client: FusedLocationProviderClient by lazy {
        FusedLocationProviderClient(context)
    }

    private val locationRequest: LocationRequest by lazy { LocationRequest() }

    private val locationCallback: LocationCallback by lazy {
        object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) =
                    locationResult?.lastLocation.let { value = it }
        }
    }

    @SuppressLint("MissingPermission")
    override fun onActive() {
        super.onActive()
        if (context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            with(locationRequest) {
                interval = TimeUnit.SECONDS.toMillis(60)
                fastestInterval = TimeUnit.SECONDS.toMillis(5)
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }

            client.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper())
        }
    }

    override fun onInactive() {
        super.onInactive()
        client.removeLocationUpdates(locationCallback)
    }
}