package de.uni_marburg.mathematik.ds.serval.model.location

import android.app.Application
import android.arch.lifecycle.AndroidViewModel

/**
 * Created by thames1990 on 18.01.18.
 */
class LocationViewModel(application: Application) : AndroidViewModel(application) {

    val locationLiveData = LocationLiveData(application)
}