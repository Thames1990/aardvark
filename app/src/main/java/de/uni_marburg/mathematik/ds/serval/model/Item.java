package de.uni_marburg.mathematik.ds.serval.model;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public abstract class Item implements Serializable {

    static final long serialVersionUID = 2L;

    private long time;
    private List<Measurement> measurements;
    private Location location;

    Item(long time, List<Measurement> measurements, Location location) {
        this.time = time;
        this.measurements = measurements;
        this.location = location;
    }

    public long getTime() {
        return time;
    }

    public List<Measurement> getMeasurements() {
        return measurements;
    }

    public Location getLocation() {
        return location;
    }

    public static class Location implements Serializable {

        static final long serialVersionUID = 3L;

        private double latitude;
        private double longitude;
        private String geohash;

        public Location(double latitude, double longitude, @Nullable String geohash) {
            // TODO Add geohash check
            this.latitude = latitude;
            this.longitude = longitude;
            if (geohash != null) {
                this.geohash = geohash;
            } else {
                // Only used when location is copied from Google Play Services Location to
                // determine distance to last known location
                this.geohash = "";
            }
        }

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

    static class Measurement implements Serializable {

        static final long serialVersionUID = 4L;

        private int value;
        private MeasurementType type;

        public Measurement(int value, MeasurementType type) {
            this.value = value;
            this.type = type;
        }

        public int getValue() {
            return value;
        }

        public MeasurementType getType() {
            return type;
        }

        enum MeasurementType {
            @SerializedName("radiation")
            RADIATION,
            @SerializedName("temperature")
            TEMPERATURE
        }
    }


}
