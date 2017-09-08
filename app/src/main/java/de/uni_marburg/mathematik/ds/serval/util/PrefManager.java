package de.uni_marburg.mathematik.ds.serval.util;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.design.widget.BottomSheetDialog;

import de.uni_marburg.mathematik.ds.serval.view.activities.IntroActivity;

/**
 * Is used to store key/value pairs permanently.
 * <p>
 * TODO JavaDoc
 */
public class PrefManager {
    
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    
    /**
     * Desired preference file.
     * <p>
     * If a preference file by this name does not exist, it will be created when you retrieve an
     * editor (SharedPreferences.edit()) and then commit changes (Editor.commit()).
     */
    private static final String PREF_NAME = "Serval";
    
    /**
     * Operating mode.
     * <p>
     * Value is either 0 or combination of MODE_PRIVATE, MODE_WORLD_READABLE, MODE_WORLD_WRITEABLE
     * or MODE_MULTI_PROCESS.
     */
    private static final int PRIVATE_MODE = 0;
    
    /**
     * Key for the boolean, that sets, if the app was started before.
     * <p>
     * This is used to check whether the {@link IntroActivity intro activity} needs to be started.
     */
    private static final String IS_FIRST_TIME_LAUNCH = "IS_FIRST_TIME_LAUNCH";
    
    /**
     * Key for the last known version code.
     * <p>
     * Is used to determine whether the changelog should be shown.
     */
    private static final String LAST_KNOWN_VERSION_CODE = "LAST_KNOWN_VERSION_CODE";
    
    private static final String SHOW_CHANGELOG = "SHOW_CHANGELOG";
    
    /**
     * This key is used to determine wheter {@link BottomSheetDialog bottom sheets dialogs} or
     * {@link Dialog dialogs} should be used.
     */
    private static final String USE_BOTTOM_SHEET_DIALOGS = "USE_BOTTOM_SHEET_DIALOGS";
    
    private static final String REQUEST_LOCATION_UPDATES = "REQUEST_LOCATION_UPDATES";
    
    private static final String USE_LINEAR_LAYOUT_MANAGER = "USE_LINEAR_LAYOUT_MANAGER";
    
    private static final String USE_GRID_LAYOUT_MANAGER = "USE_GRID_LAYOUT_MANAGER";
    
    private static final String USE_STAGGERED_GRID_LAYOUT_MANAGER =
            "USE_STAGGERED_GRID_LAYOUT_MANAGER";
    
    private static final String GRID_LAYOUT_MANAGER_SPAN_COUNT = "GRID_LAYOUT_MANAGER_SPAN_COUNT";
    
    private static final String STAGGERED_GRID_LAYOUT_MANAGER_SPAN_COUNT
            = "STAGGERED_GRID_LAYOUT_MANAGER_SPAN_COUNT";
    
    private static final String BOTTOM_NAVIGATION_SELECTED_ITEM_ID =
            "BOTTOM_NAVIGATION_SELECTED_ITEM_ID";
    
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
    
    public void setShowChangelog(boolean showChangelog) {
        editor.putBoolean(SHOW_CHANGELOG, showChangelog).commit();
    }
    
    public boolean showChangelog() {
        return preferences.getBoolean(SHOW_CHANGELOG, true);
    }
    
    public void setUseBottomSheetDialogs(boolean useBottomSheetDialogs) {
        editor.putBoolean(USE_BOTTOM_SHEET_DIALOGS, useBottomSheetDialogs).commit();
    }
    
    public boolean useBottomSheetDialogs() {
        return preferences.getBoolean(USE_BOTTOM_SHEET_DIALOGS, true);
    }
    
    public void setRequestLocationUpdates(boolean requestLocationUpdates) {
        editor.putBoolean(REQUEST_LOCATION_UPDATES, requestLocationUpdates).commit();
    }
    
    public boolean requestLocationUpdates() {
        return preferences.getBoolean(REQUEST_LOCATION_UPDATES, true);
    }
    
    public void setUseLinearLayoutManager() {
        editor.putBoolean(USE_LINEAR_LAYOUT_MANAGER, false)
              .putBoolean(USE_GRID_LAYOUT_MANAGER, true)
              .putBoolean(USE_STAGGERED_GRID_LAYOUT_MANAGER, false)
              .commit();
    }
    
    public boolean useLinearLayoutManger() {
        return preferences.getBoolean(USE_LINEAR_LAYOUT_MANAGER, true);
    }
    
    public void setUseGridLayoutManager() {
        editor.putBoolean(USE_LINEAR_LAYOUT_MANAGER, true)
              .putBoolean(USE_GRID_LAYOUT_MANAGER, false)
              .putBoolean(USE_STAGGERED_GRID_LAYOUT_MANAGER, false)
              .commit();
    }
    
    public boolean useGridLayoutManger() {
        return preferences.getBoolean(USE_GRID_LAYOUT_MANAGER, false);
    }
    
    public void setUseStaggeredGridLayoutManager() {
        editor.putBoolean(USE_LINEAR_LAYOUT_MANAGER, false)
              .putBoolean(USE_GRID_LAYOUT_MANAGER, false)
              .putBoolean(USE_STAGGERED_GRID_LAYOUT_MANAGER, true)
              .commit();
    }
    
    public boolean useStaggeredGridLayoutManger() {
        return preferences.getBoolean(USE_STAGGERED_GRID_LAYOUT_MANAGER, false);
    }
    
    public void setGridLayoutManagerSpanCount(boolean gridLayoutManagerSpanCount) {
        editor.putBoolean(GRID_LAYOUT_MANAGER_SPAN_COUNT, gridLayoutManagerSpanCount).commit();
    }
    
    public int getGridLayoutManagerSpanCount() {
        return preferences.getInt(GRID_LAYOUT_MANAGER_SPAN_COUNT, 2);
    }
    
    public void setStaggeredGridLayoutManagerSpanCount(boolean staggeredGridLayoutManagerSpanCount) {
        editor.putBoolean(
                STAGGERED_GRID_LAYOUT_MANAGER_SPAN_COUNT,
                staggeredGridLayoutManagerSpanCount
        ).commit();
    }
    
    public int getStaggeredGridLayoutManagerSpanCount() {
        return preferences.getInt(STAGGERED_GRID_LAYOUT_MANAGER_SPAN_COUNT, 2);
    }
    
    public void setBottomNavigationSelectedItemId(int selectedItemId) {
        editor.putInt(BOTTOM_NAVIGATION_SELECTED_ITEM_ID, selectedItemId).commit();
    }
    
    public int getBottomNavigationSelectedItemId() {
        return preferences.getInt(BOTTOM_NAVIGATION_SELECTED_ITEM_ID, 0);
    }
}
