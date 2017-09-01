package de.uni_marburg.mathematik.ds.serval.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Is used to store the location of an {@link Event event}.
 */
class GeohashLocation implements Parcelable {
    
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
    
    double getLatitude() {
        return latitude;
    }
    
    double getLongitude() {
        return longitude;
    }
    
    public String getGeohash() {
        return geohash;
    }
    
    protected GeohashLocation(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        geohash = in.readString();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(geohash);
    }
    
    public static final Creator<GeohashLocation> CREATOR = new Creator<GeohashLocation>() {
        @Override
        public GeohashLocation createFromParcel(Parcel in) {
            return new GeohashLocation(in);
        }
        
        @Override
        public GeohashLocation[] newArray(int size) {
            return new GeohashLocation[size];
        }
    };
}
