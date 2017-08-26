package de.uni_marburg.mathematik.ds.serval.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import de.uni_marburg.mathematik.ds.serval.view.activities.WelcomeActivity;

/**
 * Is used to store key/value pairs permanently.
 */
public class PrefManager {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    /**
     * Desired preferences file. If a preferences file by this name does not exist, it will be
     * created when you retrieve an editor (SharedPreferences.edit()) and then commit changes
     * (Editor.commit()).
     */
    private static final String PREF_NAME = "Serval";

    /**
     * Operating mode.
     * Value is either 0 or combination of MODE_PRIVATE, MODE_WORLD_READABLE,
     * MODE_WORLD_WRITEABLE or MODE_MULTI_PROCESS.
     */
    private static final int PRIVATE_MODE = 0;

    /**
     * Key for the boolean, that sets, if the app was started before.
     * <p>
     * This is used to check whether the {@link WelcomeActivity welcome activity} needs to be
     * started.
     */
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
