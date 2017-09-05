package de.uni_marburg.mathematik.ds.serval.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

/**
 * Is used to store the location of an {@link Event event}.
 */
class GeohashLocation implements Parcelable {
    
    @Json(name = "latitude")
    private Double latitude;
    
    @Json(name = "longitude")
    private Double longitude;
    
    @Json(name = "geohash")
    private String geohash;
    
    public final static Parcelable.Creator<GeohashLocation> CREATOR =
            new Creator<GeohashLocation>() {
                public GeohashLocation createFromParcel(Parcel in) {
                    GeohashLocation instance = new GeohashLocation();
                    instance.latitude = ((Double) in.readValue((Double.class.getClassLoader())));
                    instance.longitude = ((Double) in.readValue((Double.class.getClassLoader())));
                    instance.geohash = ((String) in.readValue((String.class.getClassLoader())));
                    return instance;
                }
                
                public GeohashLocation[] newArray(int size) {
                    return (new GeohashLocation[size]);
                }
            };
    
    public Double getLatitude() {
        return latitude;
    }
    
    public Double getLongitude() {
        return longitude;
    }
    
    public String getGeohash() {
        return geohash;
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(latitude);
        parcel.writeValue(longitude);
        parcel.writeValue(geohash);
    }
}
