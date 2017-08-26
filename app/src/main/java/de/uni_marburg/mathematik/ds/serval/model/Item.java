package de.uni_marburg.mathematik.ds.serval.model;

import android.location.Location;
import android.location.LocationManager;
import android.util.Log;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

import static de.uni_marburg.mathematik.ds.serval.R.string.latitude;
import static de.uni_marburg.mathematik.ds.serval.R.string.longitude;

public abstract class Item implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("measurements")
    @Expose
    private List<Measurement> measurements;
    @SerializedName("location")
    @Expose
    private GeohashLocation geohashLocation;
    @SerializedName("time")
    @Expose
    private long time;

    public long getTime() {
        return time;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Location getLocation() {
        Log.d(this.getClass().getSimpleName(), "Lat: " + latitude + ", Lon: " + longitude);
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(this.geohashLocation.getLatitude());
        location.setLongitude(this.geohashLocation.getLongitude());
        return location;
    }

}
