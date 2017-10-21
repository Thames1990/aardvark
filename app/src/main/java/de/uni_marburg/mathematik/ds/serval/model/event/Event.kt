package de.uni_marburg.mathematik.ds.serval.model.event

import android.annotation.SuppressLint
import android.location.Location
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.squareup.moshi.Json
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import kotlinx.android.parcel.Parcelize
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("ParcelCreator")
@Parcelize
data class Event(
        val time: Long,
        @Json(name = "location") private val geohashLocation: GeohashLocation,
        val measurements: List<Measurement>
) : ClusterItem, Parcelable {

    val location: Location
        get() = Location(BuildConfig.APPLICATION_ID).apply {
            latitude = geohashLocation.latitude
            longitude = geohashLocation.longitude
        }

    override fun getTitle(): String = this::class.java.simpleName

    override fun getSnippet(): String {
        val format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.SHORT,
                Locale.getDefault()
        )
        return format.format(time)
    }

    override fun getPosition(): LatLng = with(location) { LatLng(latitude, longitude) }
}

@SuppressLint("ParcelCreator")
@Parcelize
data class GeohashLocation(
        val latitude: Double,
        val longitude: Double,
        private val geohash: String
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
data class Measurement(val type: MeasurementType, val value: Int) : Parcelable

enum class MeasurementType(val res: Int, val resId: Int) {
    @Json(name = "precipitation")
    PRECIPITATION(R.string.precipitation, R.drawable.precipitation),
    @Json(name = "radiation")
    RADIATION(R.string.radiation, R.drawable.radiation),
    @Json(name = "temperature")
    TEMPERATURE(R.string.temperature, R.drawable.temperature),
    @Json(name = "wind")
    WIND(R.string.wind, R.drawable.wind)
}
