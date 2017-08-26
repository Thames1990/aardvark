package de.uni_marburg.mathematik.ds.serval.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by thames1990 on 26.08.17.
 */
public class Measurement implements Serializable {

    private static final long serialVersionUID = 1L;

    @SerializedName("type")
    @Expose
    private MeasurementType type;
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
