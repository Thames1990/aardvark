package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ProcessLifecycleOwner
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.github.ajalt.reprint.core.Reprint
import com.google.firebase.analytics.FirebaseAnalytics
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import de.uni_marburg.mathematik.ds.serval.model.event.EventDao
import de.uni_marburg.mathematik.ds.serval.model.event.EventDatabase
import de.uni_marburg.mathematik.ds.serval.utils.AuthenticationListener
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import io.fabric.sdk.android.Fabric
import java.util.*

class Aardvark : Application() {

    companion object {
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var refWatcher: RefWatcher
        lateinit var lifecycle: Lifecycle
        lateinit var authenticationListener: AuthenticationListener
        lateinit var eventDao: EventDao

        fun requireAuthentication(authenticate: Boolean = Prefs.secure_app) {
            if (authenticate) lifecycle.addObserver(authenticationListener)
            else lifecycle.removeObserver(authenticationListener)
        }
    }

    override fun onCreate() {
        super.onCreate()
        initialize()
        setupAnalytics()
        setupLeakCanary()
    }

    private fun initialize() {
        eventDao = EventDatabase.getInstance(applicationContext).eventDao()

        Prefs.initialize(this, BuildConfig.APPLICATION_ID)

        Reprint.initialize(this)
        authenticationListener = AuthenticationListener(this)
        lifecycle = ProcessLifecycleOwner.get().lifecycle
        requireAuthentication()

        val now = System.currentTimeMillis()
        Prefs.lastLaunch = now

        if (Prefs.installDate == -1L) Prefs.installDate = now
        if (Prefs.identifier == -1) Prefs.identifier = Random().nextInt(Int.MAX_VALUE)
    }

    private fun setupAnalytics() {
        // Only use analytics on release versions and if the user accepted
        if (!BuildConfig.DEBUG || !Prefs.analytics) {
            Fabric.with(this, Crashlytics(), Answers())
            Crashlytics.setUserIdentifier(Prefs.aardvarkId)
        }

        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG || Prefs.analytics)
    }

    private fun setupLeakCanary() {
        refWatcher = when (BuildConfig.DEBUG) {
            true -> LeakCanary.install(this)
            false -> RefWatcher.DISABLED
        }
    }
}
