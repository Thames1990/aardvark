package de.uni_marburg.mathematik.ds.serval.util;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Reads a changelog from assets.
 */
public class ChangelogUtil {
    
    /**
     * Reads a changelog from assets.
     *
     * @param context     Calling context
     * @param versionCode Version code of the app. Is used to determine the corresponding changelog
     *                    file
     * @return HTML conversion of the Markdown changelog file
     */
    public static String readChangelogFromAsset(Context context, int versionCode) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            return readChangelogFromAssetKitKat(context, versionCode);
        } else {
            return readChangelogFromAssetCompat(context, versionCode);
        }
    }
    
    /**
     * Reads a changelog from assets.
     *
     * @param context     Calling context
     * @param versionCode Version code of the app. Is used to determine the corresponding changelog
     *                    file
     * @return HTML conversion of the Markdown changelog file
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private static String readChangelogFromAssetKitKat(Context context, int versionCode) {
        try {
            try (InputStream input = context.getAssets().open(String.format(
                    context.getString(R.string.file_changelog),
                    versionCode
            ))) {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                StringBuilder content = new StringBuilder(input.available());
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                    content.append(System.getProperty("line.separator"));
                }
                return content.toString();
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        
        return null;
    }
    
    /**
     * Reads a changelog from assets.
     *
     * @param context     Calling context
     * @param versionCode Version code of the app. Is used to determine the corresponding changelog
     *                    file
     * @return HTML conversion of the Markdown changelog file
     */
    @SuppressWarnings("TryFinallyCanBeTryWithResources")
    private static String readChangelogFromAssetCompat(Context context, int versionCode) {
        try {
            InputStream input = context.getAssets().open(String.format(
                    context.getString(R.string.file_changelog),
                    versionCode
            ));
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input));
                StringBuilder content = new StringBuilder(input.available());
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    content.append(line);
                    content.append(System.getProperty("line.separator"));
                }
                return content.toString();
            } finally {
                input.close();
            }
        } catch (IOException e) {
            Crashlytics.logException(e);
        }
        
        return null;
    }
    
}
