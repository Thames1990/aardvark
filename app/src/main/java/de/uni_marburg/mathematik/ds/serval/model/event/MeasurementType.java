package de.uni_marburg.mathematik.ds.serval.model.event;

import android.content.Context;
import android.content.res.Resources;

import com.squareup.moshi.Json;

import java.io.Serializable;
import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Type of a {@link Measurement measurement} of an {@link Event event}.
 */
public enum MeasurementType implements Serializable {
    
    @Json(name = "precipitation")
    PRECIPITATION,
    @Json(name = "radiation")
    RADIATION,
    @Json(name = "temperature")
    TEMPERATURE,
    @Json(name = "wind")
    WIND;
    
    public int getResId(Context context) {
        int resId = context.getResources()
                           .getIdentifier(toString(), "drawable", context.getPackageName());
        if (resId == 0) {
            throw new Resources.NotFoundException(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.exception_measurement_type_without_icon),
                    toString()
            ));
        }
        return resId;
    }
    
    @Override
    public String toString() {
        return name().toLowerCase();
    }
}
