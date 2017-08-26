package de.uni_marburg.mathematik.ds.serval.model;

import android.location.Location;
import android.location.LocationManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

/**
 * Events are loaded from a REST API and represent a thing that happens or takes place,
 * especially one of importance.
 */
public abstract class Event implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Measurements of the event
     */
    @SerializedName("measurements")
    @Expose
    private List<Measurement> measurements;

    /**
     * Location of the event
     */
    @SerializedName("location")
    @Expose
    private GeohashLocation geohashLocation;

    /**
     * Occurence time of the event
     */
    @SerializedName("time")
    @Expose
    private long time;

    public long getTime() {
        return time;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    /**
     * Returns the location of the event as a {@link Location Android location}.
     * <p>
     * This is useful, because this type of event includes its util methods, such as
     * {@link Location#distanceTo(Location)}.
     *
     * @return The location of the vent
     */
    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(geohashLocation.getLatitude());
        location.setLongitude(geohashLocation.getLongitude());
        return location;
    }

}
