package de.uni_marburg.mathematik.ds.serval.model;

import com.google.gson.annotations.SerializedName;

/**
 * Type of a {@link Measurement measurement} of an {@link Event event}.
 */
enum MeasurementType {
    @SerializedName("radiation")
    RADIATION,
    @SerializedName("temperature")
    TEMPERATURE
}
