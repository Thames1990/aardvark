package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.github.ajalt.reprint.core.Reprint
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
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
        setupCrashlytics()
        setupFirebaseAnalytics()
        setupLeakCanary()
    }

    private fun initialize() {
        Reprint.initialize(this)
        Prefs.initialize(this, BuildConfig.APPLICATION_ID)

        if (Prefs.installDate == -1L) Prefs.installDate = System.currentTimeMillis()
        if (Prefs.identifier == -1) Prefs.identifier = Random().nextInt(Int.MAX_VALUE)
    }

    private fun setupCrashlytics() {
        if (!BuildConfig.DEBUG || !Prefs.useAnalytics) {
            Fabric.with(this, Crashlytics(), Answers())
            Crashlytics.setUserIdentifier(Prefs.aardvarkId)
        }
    }

    private fun setupFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(
                !BuildConfig.DEBUG || Prefs.useAnalytics
        )
    }

    private fun setupLeakCanary() {
        refWatcher = when (BuildConfig.DEBUG) {
            true  -> LeakCanary.install(this)
            false -> RefWatcher.DISABLED
        }
    }
}
