package de.uni_marburg.mathematik.ds.serval.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Measurement of a {@link Event event}.
 */
class Measurement implements Serializable {

    private static final long serialVersionUID = 1L;

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
}
