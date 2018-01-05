package de.uni_marburg.mathematik.ds.serval.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.content.Intent
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.activities.FingerprintActivity

/** Created by thames1990 on 03.12.17. */
class AuthenticationListener(val aardvark: Aardvark) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun authenticate() {
        val fingerprintIntent = Intent(aardvark, FingerprintActivity::class.java)
        fingerprintIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        aardvark.startActivity(fingerprintIntent)
    }
}