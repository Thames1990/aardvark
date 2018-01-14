package de.uni_marburg.mathematik.ds.serval.model.event

import android.annotation.SuppressLint
import android.arch.persistence.room.Entity
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
@Entity(tableName = "events", primaryKeys = ["time", "geohashLocation"])
@Parcelize
data class Event(
        val time: Long,
        @Json(name = "location")
        val geohashLocation: GeohashLocation,
        val measurements: List<Measurement>
) : ClusterItem, Parcelable {

    /** Generates a [location][Location] from the [geohash location][geohashLocation]. **/
    val location: Location
        get() = Location(BuildConfig.APPLICATION_ID).apply {
            latitude = geohashLocation.latitude
            longitude = geohashLocation.longitude
        }

    /** Title of the event used for map info windows **/
    override fun getTitle(): String = javaClass.simpleName

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
 * @param textRes Localized name of the measrurement type
 * @param formatRes Localized format of the measurement type
 * @param iconRes Image resource of the measurement type
 */
// TODO Replace icon drawable with IIcon from Weather Icons
enum class MeasurementType(val textRes: Int, val formatRes: Int, val iconRes: Int) {
    /** Signals that rainfall or snowfall was measured */
    @Json(name = "precipitation")
    PRECIPITATION(
            textRes = R.string.precipitation,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.precipitation
    ),
    /** Signals that radiation was measured */
    @Json(name = "radiation")
    RADIATION(
            textRes = R.string.radiation,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.radiation
    ),
    /** Signals that temperature was measured */
    @Json(name = "temperature")
    TEMPERATURE(
            textRes = R.string.temperature,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.temperature
    ),
    /** Signals that wind was measured */
    @Json(name = "wind")
    WIND(
            textRes = R.string.wind,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.wind
    )
}
