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

/**
 * An event is an action or occurence of measurements recognized by sensors.
 *
 * @param time Occurence time of the event
 * @param geohashLocation Location of the event with latitude, longitude and geohash
 * @param measurements Measurements recorded by the event
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Event(
        val time: Long,
        @Json(name = "location") private val geohashLocation: GeohashLocation,
        val measurements: List<Measurement>
) : ClusterItem, Parcelable {

    /** Generates a [location][Location] from the [geohash location][geohashLocation]. **/
    val location: Location
        get() = Location(BuildConfig.APPLICATION_ID).apply {
            latitude = geohashLocation.latitude
            longitude = geohashLocation.longitude
        }

    /** Title of the event used for map info windows **/
    override fun getTitle(): String = this::class.java.simpleName

    /** Snippet of the event used for map info windows **/
    override fun getSnippet(): String {
        val format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.SHORT,
                Locale.getDefault()
        )
        return format.format(time)
    }

    /** Snippet of the event used for map markers **/
    override fun getPosition(): LatLng = with(location) { LatLng(latitude, longitude) }
}

/**
 * Location of an [event][Event] with geohash.
 *
 * @param latitude Latitude of an event
 * @param longitude Longitude of an event
 * @param geohash Geohash of the position of an event
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class GeohashLocation(
        val latitude: Double,
        val longitude: Double,
        private val geohash: String
) : Parcelable

/**
 * Measurement recognized by sensors
 *
 * @param type Type of the measurement
 * @param value Value of the measurement
 */
@SuppressLint("ParcelCreator")
@Parcelize
data class Measurement(val type: MeasurementType, val value: Int) : Parcelable

/**
 * Measurement type of a [measurement][Measurement]
 *
 * @param res Localized name of the measrurement type
 * @param resFormat Localized format of the measurement type
 * @param resId Image resource of the measurement type
 */
// TODO Replace icon drawable with IIcon from Weather Icons
enum class MeasurementType(val res: Int, val resFormat: Int, val resId: Int) {
    /** Signals that rainfall or snowfall was measured */
    @Json(name = "precipitation")
    PRECIPITATION(
            R.string.precipitation,
            R.string.measurement_value_precipitation,
            R.drawable.precipitation
    ),
    /** Signals that radiation was measured */
    @Json(name = "radiation")
    RADIATION(
            R.string.radiation,
            R.string.measurement_value_precipitation,
            R.drawable.radiation
    ),
    /** Signals that temperature was measured */
    @Json(name = "temperature")
    TEMPERATURE(
            R.string.temperature,
            R.string.measurement_value_precipitation,
            R.drawable.temperature
    ),
    /** Signals that wind was measured */
    @Json(name = "wind")
    WIND(
            R.string.wind,
            R.string.measurement_value_precipitation,
            R.drawable.wind
    )
}
