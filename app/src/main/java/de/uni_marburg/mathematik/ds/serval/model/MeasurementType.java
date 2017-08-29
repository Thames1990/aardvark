package de.uni_marburg.mathematik.ds.serval.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Type of a {@link Measurement measurement} of an {@link Event event}.
 */
public enum MeasurementType implements Serializable {

    @SerializedName("radiation")
    RADIATION,
    @SerializedName("temperature")
    TEMPERATURE;

    /**
     * Resolves the resource identifier with the same name.
     *
     * @param context Interface to global information about an application environment. This is
     *                an abstract class whose implementation is provided by the Android system.
     *                It allows access to application-specific resources and classes, as well as
     *                up-calls for application-level operations such as launching activities,
     *                broadcasting and receiving intents, etc.
     * @return The resource identifier for the resource with the same name
     */
    public int getResId(Context context) {
        return context.getResources().getIdentifier(
                toString(),
                "drawable",
                context.getPackageName()
        );
    }

    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
