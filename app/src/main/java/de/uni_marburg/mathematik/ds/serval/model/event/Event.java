package de.uni_marburg.mathematik.ds.serval.model.event;

import android.location.Location;
import android.os.Parcelable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.squareup.moshi.Json;

import java.util.ArrayList;
import java.util.List;

/**
 * Events are loaded with {@link de.uni_marburg.mathematik.ds.serval.controller.tasks.EventAsyncTask
 * an asynchronous task} and represent a thing that happens or takes place, especially one of
 * importance.
 */
public abstract class Event implements ClusterItem, Parcelable {
    
    /**
     * Time of the event in Unix time
     */
    @Json(name = "time")
    Long time;
    
    /**
     * Location of the event, including a geohash.
     */
    @Json(name = "location")
    GeohashLocation location;
    
    /**
     * {@link Measurement Measurements} of the event
     */
    @Json(name = "measurements")
    List<Measurement> measurements = new ArrayList<>();
    
    @Override
    public LatLng getPosition() {
        Location location = getLocation();
        return new LatLng(location.getLatitude(), location.getLongitude());
    }
    
    public Long getTime() {
        return time;
    }
    
    public GeohashLocation getGeohashLocation() {
        return location;
    }
    
    /**
     * Converts the {@link Event#location} into a {@link Location Android location}.
     *
     * @return {@link Location Android location of the event}
     */
    public Location getLocation() {
        Location location = new Location("");
        location.setLatitude(this.location.getLatitude());
        location.setLongitude(this.location.getLongitude());
        return location;
    }
    
    public List<Measurement> getMeasurements() {
        return measurements;
    }
}
