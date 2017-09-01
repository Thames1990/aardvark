package de.uni_marburg.mathematik.ds.serval.model;

import android.location.Location;
import android.location.LocationManager;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Events are loaded from a REST API and represent a thing that happens or takes place,
 * especially one of importance.
 */
public abstract class Event implements Parcelable {
    
    /**
     * Measurements of the event
     */
    @SerializedName("measurements")
    @Expose
    List<Measurement> measurements = new ArrayList<>();
    
    /**
     * Location of the event
     */
    @SerializedName("location")
    @Expose
    GeohashLocation geohashLocation;
    
    /**
     * Occurence time of the event
     */
    @SerializedName("time")
    @Expose
    long time;
    
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
