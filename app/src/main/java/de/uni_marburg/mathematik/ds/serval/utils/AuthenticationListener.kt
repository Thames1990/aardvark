package de.uni_marburg.mathematik.ds.serval.utils

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import de.uni_marburg.mathematik.ds.serval.activities.FingerprintActivity

class AuthenticationListener(private val application: Application) : LifecycleObserver {

    /**
     * Requires fingerprint authentication and therefore opens
     * [the fingerprint authentication activity][FingerprintActivity].
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun requireFingerprintAuthentication() {
        val fingerprintIntent = Intent(application, FingerprintActivity::class.java)
        fingerprintIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        application.startActivity(fingerprintIntent)
    }
}