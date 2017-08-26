package de.uni_marburg.mathematik.ds.serval.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by thames1990 on 26.08.17.
 */

public class GeohashLocation implements Serializable {

    private final static long serialVersionUID = 1L;

    @SerializedName("latitude")
    @Expose
    private double latitude;
    @SerializedName("longitude")
    @Expose
    private double longitude;
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
