package de.uni_marburg.mathematik.ds.serval.model.location

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val location = LocationLiveData(application.applicationContext)
}