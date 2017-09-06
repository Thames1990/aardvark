package de.uni_marburg.mathematik.ds.serval.util;

import android.location.Location;

import java.util.concurrent.TimeUnit;

/**
 * Created by thames1990 on 01.09.17.
 */
public class LocationUtil {
    
    private static final long TWO_MINUTES = TimeUnit.MINUTES.toMillis(2);
    
    public static boolean isBetterLocation(Location location, Location currentLocation) {
        if (currentLocation == null) {
            return true;
        }
        
        long timeDelta = location.getTime() - currentLocation.getTime();
        
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;
        
        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }
        
        int accuracyDelta = (int) (location.getAccuracy() - currentLocation.getAccuracy());
        
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;
        boolean isFromSameProvider =
                isSameProvider(location.getProvider(), currentLocation.getProvider());
        
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }
    
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
    
}
