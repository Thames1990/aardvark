package de.uni_marburg.mathematik.ds.serval.model.event;

import android.location.Location;
import android.os.Parcelable;

import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Events are loaded from a REST API and represent a thing that happens or takes place,
 * especially one of importance.
 */
public abstract class Event implements Parcelable {
    
    @Json(name = "time")
    Long time;
    
    @Json(name = "location")
    GeohashLocation location;
    
    @Json(name = "measurements")
    List<Measurement> measurements = new ArrayList<>();
    
    public Long getTime() {
        return time;
    }
    
    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(this.location.getLatitude());
        location.setLongitude(this.location.getLongitude());
        return location;
    }
    
    public List<Measurement> getMeasurements() {
        return measurements;
    }
    
    public enum EventComparator {
        TIME_ASCENDING,
        TIME_DESCENDING,
        DISTANCE_ASCENDING,
        DISTANCE_DESCENDING
    }
}
