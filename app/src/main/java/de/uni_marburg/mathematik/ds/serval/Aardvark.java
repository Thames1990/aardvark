package de.uni_marburg.mathematik.ds.serval;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import io.fabric.sdk.android.Fabric;

/**
 * Created by thames1990 on 04.09.17.
 */
public class Aardvark extends Application {
    
    private PrefManager preferences;
    
    private Fabric fabric;
    
    private RefWatcher refWatcher;
    
    private FirebaseAnalytics firebaseAnalytics;
    
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        setupPrefManager();
        installLeakCanary();
        installCrashlytics();
        installFirebaseAnalytics();
    }
    
    private void setupPrefManager() {
        preferences = new PrefManager(this);
    }
    
    private void installLeakCanary() {
        if (BuildConfig.DEBUG) {
            refWatcher = LeakCanary.install(this);
        } else {
            refWatcher = RefWatcher.DISABLED;
        }
    }
    
    private void installCrashlytics() {
        fabric = Fabric.with(
                this,
                new Crashlytics.Builder().core(
                        new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()
                ).build()
        );
    }
    
    private void installFirebaseAnalytics() {
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(false);
    }
    
    public static PrefManager getPreferences(Context context) {
        Aardvark aardvark = (Aardvark) context.getApplicationContext();
        return aardvark.preferences;
    }
    
    public static Fabric getFabric(Context context) {
        Aardvark aardvark = (Aardvark) context.getApplicationContext();
        return aardvark.fabric;
    }
    
    public static RefWatcher getRefWatcher(Context context) {
        Aardvark aardvark = (Aardvark) context.getApplicationContext();
        return aardvark.refWatcher;
    }
    
    public static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        Aardvark aardvark = (Aardvark) context.getApplicationContext();
        return aardvark.firebaseAnalytics;
    }
}
