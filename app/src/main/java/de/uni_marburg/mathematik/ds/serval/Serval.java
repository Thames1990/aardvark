package de.uni_marburg.mathematik.ds.serval;

import android.app.Application;
import android.content.Context;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.fabric.sdk.android.Fabric;

/**
 * Created by thames1990 on 04.09.17.
 */
public class Serval extends Application {
    
    private Fabric fabric;
    
    private RefWatcher refWatcher;
    
    private FirebaseAnalytics firebaseAnalytics;
    
    public static Fabric getFabric(Context context) {
        Serval serval = (Serval) context.getApplicationContext();
        return serval.fabric;
    }
    
    public static RefWatcher getRefWatcher(Context context) {
        Serval serval = (Serval) context.getApplicationContext();
        return serval.refWatcher;
    }
    
    public static FirebaseAnalytics getFirebaseAnalytics(Context context) {
        Serval serval = (Serval) context.getApplicationContext();
        return serval.firebaseAnalytics;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            return;
        }
        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        fabric = Fabric.with(this, new Crashlytics.Builder().core(core).build());
        refWatcher = LeakCanary.install(this);
        firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        firebaseAnalytics.setAnalyticsCollectionEnabled(false);
    }
}
