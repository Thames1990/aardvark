package de.uni_marburg.mathematik.ds.serval.model.event

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.os.Parcelable
import ca.allanwang.kau.utils.string
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.squareup.moshi.Json
import de.uni_marburg.mathematik.ds.serval.R
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class Event(
        val time: Long,
        @Json(name = "location")
        private val geohashLocation: GeohashLocation,
        val measurements: List<Measurement>
) : ClusterItem, Parcelable {

    val location: Location
        get() {
            val location = Location("")
            location.latitude = geohashLocation.latitude
            location.longitude = geohashLocation.longitude
            return location
        }

    override fun getPosition(): LatLng = LatLng(location.latitude, location.longitude)

    override fun getSnippet(): String {
        val format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.SHORT,
                Locale.getDefault()
        )
        return format.format(time)
    }

    override fun getTitle(): String = "Event"
}

@Parcelize
data class GeohashLocation(
        val latitude: Double,
        val longitude: Double,
        private val geohash: String
) : Parcelable

@Parcelize
data class Measurement(val type: MeasurementType, val value: Int) : Parcelable

enum class MeasurementType {
    @Json(name = "precipitation")
    PRECIPITATION,
    @Json(name = "radiation")
    RADIATION,
    @Json(name = "temperature")
    TEMPERATURE,
    @Json(name = "wind")
    WIND;

    /** Computes the resource id of this measurement type. **/
    fun getResId(context: Context): Int {
        val resId = context.resources.getIdentifier(
                name.toLowerCase(),
                "drawable",
                context.packageName
        )
        if (resId == 0) {
            throw Resources.NotFoundException(String.format(
                    Locale.getDefault(),
                    context.string(R.string.exception_measurement_type_without_icon),
                    toString()
            ))
        }
        return resId
    }

    override fun toString(): String = name[0] + name.substring(1).toLowerCase()
}
