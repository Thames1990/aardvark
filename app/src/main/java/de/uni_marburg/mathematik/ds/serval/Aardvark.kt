package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
import com.github.ajalt.reprint.core.Reprint
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import io.fabric.sdk.android.Fabric

class Aardvark : Application() {

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var refWatcher: RefWatcher
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
        setupCrashlytics()
        setupFirebaseAnalytics()
        setupLeakCanary()
    }

    private fun initialize() {
        Reprint.initialize(this)
        Preferences.initialize(this, BuildConfig.APPLICATION_ID)
    }

    private fun setupCrashlytics() {
        Fabric.with(this, Crashlytics.Builder().core(
                CrashlyticsCore.Builder().disabled(
                        BuildConfig.DEBUG || !Preferences.useAnalytics
                ).build()
        ).build())
    }

    private fun setupFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(
                !BuildConfig.DEBUG || Preferences.useAnalytics
        )
    }

    private fun setupLeakCanary() {
        refWatcher = when (BuildConfig.DEBUG) {
            true  -> LeakCanary.install(this)
            false -> RefWatcher.DISABLED
        }
    }
}
