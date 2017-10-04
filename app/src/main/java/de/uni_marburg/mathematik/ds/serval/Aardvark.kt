package de.uni_marburg.mathematik.ds.serval

import android.app.Application
import ca.allanwang.kau.logging.KL
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
        lateinit var fabric: Fabric
        lateinit var firebaseAnalytics: FirebaseAnalytics
        lateinit var refWatcher: RefWatcher
    }

    override fun onCreate() {
        super.onCreate()
        KL.debug(BuildConfig.DEBUG)
        Preferences.initialize(this, string(R.string.app_name))
        fabric = Fabric.with(this, Crashlytics.Builder().core(
                CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
        ).build())
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        firebaseAnalytics.setAnalyticsCollectionEnabled(false)
        refWatcher = when (BuildConfig.DEBUG) {
            true  -> LeakCanary.install(this)
            false -> RefWatcher.DISABLED
        }
    }
}
