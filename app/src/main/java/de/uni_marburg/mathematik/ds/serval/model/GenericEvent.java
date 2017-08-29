package de.uni_marburg.mathematik.ds.serval.model;

import android.os.Parcel;
import android.support.annotation.NonNull;

/**
 * This is just a generic implementation of {@link Event} and has no extensions or modifications
 * so far.
 */
public class GenericEvent extends Event {

    protected GenericEvent(Parcel in) {
        in.readList(measurements, Measurement.class.getClassLoader());
        geohashLocation = in.readParcelable(GeohashLocation.class.getClassLoader());
        time = in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeList(measurements);
        dest.writeParcelable(geohashLocation, flags);
        dest.writeLong(time);
    }

    public static final Creator<GenericEvent> CREATOR = new Creator<GenericEvent>() {
        @Override
        public GenericEvent createFromParcel(Parcel in) {
            return new GenericEvent(in);
        }

        @Override
        public GenericEvent[] newArray(int size) {
            return new GenericEvent[size];
        }
    };
}
