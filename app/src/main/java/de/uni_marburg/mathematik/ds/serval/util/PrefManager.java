package de.uni_marburg.mathematik.ds.serval.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;

import de.uni_marburg.mathematik.ds.serval.view.activities.IntroActivity;

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
     * This is used to check whether the {@link IntroActivity intro activity} needs to be
     * started.
     */
    private static final String IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH";

    /**
     * Key for the last known version code. Is used to determine whether the changelog should be
     * shown.
     */
    private static final String LAST_KNOWN_VERSION_CODE = "LAST_KNOWN_VERSION_CODE";

    /**
     * This key is used to determine wheter {@link BottomSheetDialog bottom sheets dialogs} or
     * {@link Dialog dialogs} should be used.
     */
    private static final String USE_BOTTOM_SHEET_DIALOGS = "USE_BOTTOM_SHEET_DIALOGS";

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

    public void setLastKnownVersionCode(int currentVersion) {
        editor.putInt(LAST_KNOWN_VERSION_CODE, currentVersion).commit();
    }

    public int getLastKnownVersionCode() {
        return preferences.getInt(LAST_KNOWN_VERSION_CODE, 0);
    }

    public boolean useBottomSheetDialogs() {
        return preferences.getBoolean(USE_BOTTOM_SHEET_DIALOGS, true);
    }
}
