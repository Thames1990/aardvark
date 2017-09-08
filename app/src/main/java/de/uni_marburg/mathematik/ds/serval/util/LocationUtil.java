package de.uni_marburg.mathematik.ds.serval.util;

import android.location.Location;
import android.location.LocationProvider;

import java.util.concurrent.TimeUnit;

/**
 * A utility class for {@link Location locations}.
 */
public class LocationUtil {
    
    private static final long TWO_MINUTES = TimeUnit.MINUTES.toMillis(2);
    
    /**
     * Determines whether a location is better than another. This is calculated by their time and
     * accuracy.
     *
     * @param location        The new {@link Location location}
     * @param currentLocation The current {@link Location location}
     * @return {@code True} if the new {@link Location location} is better than the current.
     */
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
    
    /**
     * Determines whether two {@link Location locations} are from the same {@link LocationProvider
     * provider}.
     * <p>
     * Each provider has a set of criteria under which it may be used; for example, some providers
     * require GPS hardware and visibility to a number of satellites; others require the use of the
     * cellular radio, or access to a specific carrier's network, or to the internet. They may also
     * have different battery consumption characteristics or monetary costs to the user.
     *
     * @param provider1 First {@link LocationProvider provider}
     * @param provider2 Second {@link LocationProvider provider}
     * @return {@code True} if the providers are the same; {@code false} otherwise.
     */
    private static boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }
    
    
}
