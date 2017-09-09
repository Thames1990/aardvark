package de.uni_marburg.mathematik.ds.serval.util;

import android.content.Context;

import com.crashlytics.android.Crashlytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Created by thames1990 on 09.09.17.
 */
public class ChangelogUtil {
    
    public static String readChangelogFromAsset(Context context, int versionCode) {
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
