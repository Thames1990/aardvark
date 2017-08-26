package de.uni_marburg.mathematik.ds.serval.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Is used to store the location of an {@link Event event}.
 */
class GeohashLocation implements Serializable {

    private final static long serialVersionUID = 1L;

    /**
     * Latitude of the event
     */
    @SerializedName("latitude")
    @Expose
    private double latitude;

    /**
     * Longitude of the event
     */
    @SerializedName("longitude")
    @Expose
    private double longitude;

    /**
     * Geohash of the event
     */
    @SerializedName("geohash")
    @Expose
    private String geohash;

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getGeohash() {
        return geohash;
    }

}
