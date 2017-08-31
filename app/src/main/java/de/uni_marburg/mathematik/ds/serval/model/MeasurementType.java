package de.uni_marburg.mathematik.ds.serval.model;

import android.content.Context;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.exceptions.MeasurementTypeWithoutIcon;

/**
 * Type of a {@link Measurement measurement} of an {@link Event event}.
 */
public enum MeasurementType implements Serializable {

    @SerializedName("radiation")
    RADIATION,
    @SerializedName("temperature")
    TEMPERATURE,
    @SerializedName("wind")
    WIND,
    @SerializedName("precipitation")
    PRECIPITATION;

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
    public int getResId(Context context) throws MeasurementTypeWithoutIcon {
        int resId = context.getResources().getIdentifier(
                toString(),
                "drawable",
                context.getPackageName()
        );
        if (resId == 0) {
            throw new MeasurementTypeWithoutIcon(String.format(
                    context.getString(R.string.measurement_type_without_icon_exception),
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
