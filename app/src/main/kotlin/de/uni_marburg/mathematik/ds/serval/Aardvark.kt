package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.ProcessLifecycleOwner
import com.crashlytics.android.Crashlytics
import com.crashlytics.android.answers.Answers
import com.github.ajalt.reprint.core.Reprint
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.iid.FirebaseInstanceId
import com.squareup.leakcanary.LeakCanary
import com.squareup.leakcanary.RefWatcher
import de.uni_marburg.mathematik.ds.serval.utils.AuthenticationListener
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInMillis
import de.uni_marburg.mathematik.ds.serval.utils.isDebugBuild
import io.fabric.sdk.android.Fabric

class Aardvark : Application() {

    companion object {
        lateinit var aardvarkId: String
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var refWatcher: RefWatcher
    }

    private lateinit var authenticationListener: AuthenticationListener
    private lateinit var lifecycle: Lifecycle

    override fun onCreate() {
        super.onCreate()
        initialize()
        setupAnalytics()
        setupLeakCanary()
    }

    private fun initialize() {
        FirebaseApp.initializeApp(applicationContext)
        aardvarkId = FirebaseInstanceId.getInstance().id

        Prefs.initialize(applicationContext, BuildConfig.APPLICATION_ID)

        Reprint.initialize(applicationContext)
        authenticationListener = AuthenticationListener(this)
        lifecycle = ProcessLifecycleOwner.get().lifecycle
        setupAuthentication()

        if (Prefs.installDate == -1L) Prefs.installDate = currentTimeInMillis
    }

    private fun setupAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext).apply {
            setAnalyticsCollectionEnabled(Prefs.Behaviour.analyticsEnabled)
        }

        if (Prefs.Behaviour.analyticsEnabled) {
            Fabric.with(applicationContext, Crashlytics(), Answers())
            Crashlytics.setUserIdentifier(aardvarkId)
        }
    }

    private fun setupLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) return
        refWatcher =
                if (isDebugBuild) LeakCanary.install(this)
                else RefWatcher.DISABLED
    }

    private fun setupAuthentication(authenticate: Boolean = Prefs.Experimental.secureApp) =
        if (authenticate) lifecycle.addObserver(authenticationListener)
        else lifecycle.removeObserver(authenticationListener)
}
