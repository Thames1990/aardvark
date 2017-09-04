package de.uni_marburg.mathematik.ds.serval;

import android.app.Application;
import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by thames1990 on 04.09.17.
 */
public class Serval extends Application {
    
    private RefWatcher refWatcher;
    
    public static RefWatcher getRefWatcher(Context context) {
        Serval serval = (Serval) context.getApplicationContext();
        return serval.refWatcher;
    }
    
    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            return;
        }
        refWatcher = LeakCanary.install(this);
    }
}
