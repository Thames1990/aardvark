package de.uni_marburg.mathematik.ds.serval.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by thames1990 on 21.08.17.
 */
public class PrefManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "Serval";

    private static final String IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH";

    @SuppressLint("CommitPrefEdits")
    public PrefManager(Context context) {
        preferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = preferences.edit();
    }

    public void setIsFirstTimeLaunch(boolean isFirstTimeLaunch) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTimeLaunch).commit();
    }

    public boolean isFirstTimeLaunch() {
        return preferences.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }
}
