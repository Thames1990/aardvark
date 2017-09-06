package de.uni_marburg.mathematik.ds.serval.model.event;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This is just a generic implementation of {@link Event} and has no extensions or modifications
 * so far.
 */
public class GenericEvent extends Event {
    
    public final static Parcelable.Creator<GenericEvent> CREATOR = new Creator<GenericEvent>() {
        public GenericEvent createFromParcel(Parcel in) {
            GenericEvent instance = new GenericEvent();
            instance.time = (Long) in.readValue(Long.class.getClassLoader());
            instance.location =
                    (GeohashLocation) in.readValue(GeohashLocation.class.getClassLoader());
            in.readList(instance.measurements, Measurement.class.getClassLoader());
            return instance;
        }
        
        public GenericEvent[] newArray(int size) {
            return (new GenericEvent[size]);
        }
    };
    
    @Override
    public int describeContents() {
        return 0;
    }
    
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeValue(time);
        parcel.writeValue(location);
        parcel.writeList(measurements);
    }
}
