package de.uni_marburg.mathematik.ds.serval.model.event;

import android.os.Parcel;
import android.os.Parcelable;

import com.squareup.moshi.Json;

/**
 * Measurement of a {@link Event event}.
 */
public class Measurement implements Parcelable {
    
    @Json(name = "type")
    private MeasurementType type;
    
    @Json(name = "value")
    private Integer value;
    
    public final static Parcelable.Creator<Measurement> CREATOR = new Creator<Measurement>() {
        public Measurement createFromParcel(Parcel in) {
            Measurement instance = new Measurement();
            instance.type = ((MeasurementType) in.readValue((MeasurementType.class.getClassLoader())));
            instance.value = ((Integer) in.readValue((Integer.class.getClassLoader())));
            return instance;
        }
        
        public Measurement[] newArray(int size) {
            return (new Measurement[size]);
        }
        
    };
    
    public MeasurementType getType() {
        return type;
    }
    
    public Integer getValue() {
        return value;
    }
    
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(type);
        dest.writeValue(value);
    }
    
    public int describeContents() {
        return 0;
    }
}
