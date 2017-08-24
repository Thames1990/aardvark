package de.uni_marburg.mathematik.ds.serval.model.util;

import de.uni_marburg.mathematik.ds.serval.model.Item;

/**
 * Created by thames1990 on 24.08.17.
 */

public class LocationUtil {

    /**
     * Earth radius in meters
     */
    private static final double EARTH_RADIUS = 6371000;

    /**
     * Calculates the distance between two points in meters.
     *
     * @param one Location one
     * @param two Location two
     * @return Distance in meters
     */
    public static double distance(Item.Location one, Item.Location two) {
        double distanceLatitude = Math.toRadians(two.getLatitude() - one.getLatitude());
        double distanceLongitude = Math.toRadians(two.getLongitude() - one.getLongitude());

        double sinDistanceLatitude = Math.sin(distanceLatitude / 2);
        double sinDistanceLongitude = Math.sin(distanceLongitude / 2);

        double a = Math.pow(sinDistanceLatitude, 2) + Math.pow(sinDistanceLongitude, 2) *
                Math.cos(Math.toRadians(one.getLatitude())) *
                Math.cos(Math.toRadians(two.getLatitude()));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS * c;
    }
}
