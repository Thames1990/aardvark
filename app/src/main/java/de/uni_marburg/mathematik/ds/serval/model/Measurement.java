package de.uni_marburg.mathematik.ds.serval.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Measurement of a {@link Event event}.
 */
public class Measurement implements Parcelable {
    
    /**
     * Type of the measurement
     */
    @SerializedName("type")
    @Expose
    private MeasurementType type;
    
    /**
     * Value of the measurement
     */
    @SerializedName("value")
    @Expose
    private int value;
    
    public int getValue() {
        return value;
    }
    
    public MeasurementType getType() {
        return type;
    }
    
    protected Measurement(Parcel in) {
        type = (MeasurementType) in.readSerializable();
        value = in.readInt();
    }
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeSerializable(type);
        dest.writeInt(value);
    }
    
    public static final Creator<Measurement> CREATOR = new Creator<Measurement>() {
        @Override
        public Measurement createFromParcel(Parcel in) {
            return new Measurement(in);
        }
        
        @Override
        public Measurement[] newArray(int size) {
            return new Measurement[size];
        }
    };
}
