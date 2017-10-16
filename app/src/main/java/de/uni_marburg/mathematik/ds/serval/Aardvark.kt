package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import ca.allanwang.kau.utils.string
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.core.CrashlyticsCore
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
        setupPreferences()
        setupCrashlytics()
        setupFirebaseAnalytics()
        setupLeakCanary()
    }

    private fun setupPreferences() {
        Preferences.initialize(this, string(R.string.app_name))
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
            true -> LeakCanary.install(this)
            false -> RefWatcher.DISABLED
        }
    }
}
