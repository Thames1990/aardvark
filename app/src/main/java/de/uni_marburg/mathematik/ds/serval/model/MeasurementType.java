package de.uni_marburg.mathematik.ds.serval.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by thames1990 on 26.08.17.
 */

public enum MeasurementType {
    @SerializedName("radiation")
    RADIATION,
    @SerializedName("temperature")
    TEMPERATURE
}
