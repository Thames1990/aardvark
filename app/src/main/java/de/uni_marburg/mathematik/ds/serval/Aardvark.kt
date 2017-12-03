package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import android.arch.lifecycle.ProcessLifecycleOwner
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.github.ajalt.reprint.core.Reprint
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import de.uni_marburg.mathematik.ds.serval.util.ForegroundBackgroundListener
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import io.fabric.sdk.android.Fabric
import java.util.*

class Aardvark : Application() {

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var refWatcher: RefWatcher
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
        setupAnalytics()
        setupLeakCanary()
    }

    private fun initialize() {
        Reprint.initialize(this)
        Prefs.initialize(this, BuildConfig.APPLICATION_ID)

        ProcessLifecycleOwner.get().lifecycle.addObserver(ForegroundBackgroundListener(this))

        val now = System.currentTimeMillis()
        Prefs.lastLaunch = now

        if (Prefs.installDate == -1L) Prefs.installDate = now
        if (Prefs.identifier == -1) Prefs.identifier = Random().nextInt(Int.MAX_VALUE)
    }

    private fun setupAnalytics() {
        // Only use analytics on release versions and if the user accepted
        if (!BuildConfig.DEBUG || !Prefs.useAnalytics) {
            Fabric.with(this, Crashlytics(), Answers())
            Crashlytics.setUserIdentifier(Prefs.aardvarkId)
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG || Prefs.useAnalytics)
    }

    private fun setupLeakCanary() {
        refWatcher = when (BuildConfig.DEBUG) {
            true  -> LeakCanary.install(this)
            false -> RefWatcher.DISABLED
        }
    }
}
