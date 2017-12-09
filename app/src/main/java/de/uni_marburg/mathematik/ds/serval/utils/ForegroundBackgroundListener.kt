package de.uni_marburg.mathematik.ds.serval.utils

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.activities.FingerprintActivity

/** Created by thames1990 on 03.12.17. */
class ForegroundBackgroundListener(val aardvark: Aardvark) : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun authenticate() {
        aardvark.startActivity(FingerprintActivity::class.java)
    }
}