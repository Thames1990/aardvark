package de.uni_marburg.mathematik.ds.serval.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.location.Location
import android.support.annotation.StringRes
import com.google.android.gms.maps.model.LatLng
import com.mikepenz.community_material_typeface_library.CommunityMaterial
import com.mikepenz.iconics.typeface.IIcon
import com.mikepenz.weather_icons_typeface_library.WeatherIcons
import com.squareup.moshi.Json
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInSeconds
import net.sharewire.googlemapsclustering.ClusterItem

/**
 * An event is something which happens or takes place. An event is brief, possibly extremely brief.
 *
 * @property id Identifier, created from a SHA3-256 hash of [data]
 * @property data Describes this event
 */
@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val data: Data
) : ClusterItem {

    /**
     * Occurrence time in seconds
     */
    inline val time: Long
        get() = data.time

    /**
     * Occurrence location
     */
    inline val location: Location
        get() = Location(BuildConfig.APPLICATION_ID).apply {
            latitude = data.geohashLocation.latitude
            longitude = data.geohashLocation.longitude
        }

    /**
     * Occurrence position
     */
    inline val position: LatLng
        get() = LatLng(location.latitude, location.longitude)

    /**
     * Measurements captured by this event
     *
     *
     */
    inline val measurements: List<Measurement>
        get() = data.measurements

    /**
     * Seconds passed since this event occurred
     */
    inline val passedSeconds: Long
        get() = currentTimeInSeconds - time

    override fun getSnippet(): String? = Prefs.Appearance.DateTimeFormat.format.formatTime(time)

    override fun getLongitude(): Double = data.geohashLocation.longitude

    override fun getLatitude(): Double = data.geohashLocation.latitude

    override fun getTitle(): String? = javaClass.simpleName

}

/**
 * Data generated by an [event][Event].
 *
 * @property time Occurrence time in seconds
 * @property geohashLocation Occurrence location (latitude, longitude) with geohash
 * @property measurements Measurements captured by an [event][Event]
 */
data class Data(
    val time: Long,
    @field:Json(name = "location") val geohashLocation: GeohashLocation,
    val measurements: List<Measurement>
)

/**
 * Location with a geohash.
 *
 * @property latitude Geographic coordinate that specifies the north–south position
 * @property longitude Geographic coordinate that specifies the east-west position
 * @property geohash Geographic location encoding
 */
data class GeohashLocation(val latitude: Double, val longitude: Double, private val geohash: String)

/**
 * A measurement taken by an [event][Event].
 *
 * @property type Measurement type
 * @property value Measurement value
 */
data class Measurement(val type: MeasurementType, val value: Int)

/**
 * Type of a [measurement][Measurement].
 *
 * @property titleRes Resource ID of the title
 * @property formatRes Resource ID for the format
 * @property iicon Measurement icon
 */
enum class MeasurementType(
    @StringRes val titleRes: Int,
    @StringRes val formatRes: Int,
    val iicon: IIcon
) {

    /**
     * Any product of the condensation of atmospheric water vapor that falls under gravity.
     * The main forms of precipitation include drizzle, rain, sleet, snow, graupel and hail.
     */
    @Json(name = "precipitation")
    PRECIPITATION(
        titleRes = R.string.measurement_precipitation,
        formatRes = R.string.measurement_value_precipitation,
        iicon = WeatherIcons.Icon.wic_rain
    ),

    /**
     * The emission or transmission of energy in the form of waves or particles through space or
     * through a material medium.
     */
    @Json(name = "radiation")
    RADIATION(
        titleRes = R.string.measurement_radiation,
        formatRes = R.string.measurement_value_radiation,
        iicon = CommunityMaterial.Icon.cmd_atom
    ),

    /**
     *  Physical quantity expressing hot and cold.
     */
    @Json(name = "temperature")
    TEMPERATURE(
        titleRes = R.string.measurement_temperature,
        formatRes = R.string.measurement_value_temperature,
        iicon = WeatherIcons.Icon.wic_thermometer
    ),

    /**
     * The flow of gases on a large scale. On the surface of the Earth, wind consists of the bulk
     * movement of air.
     */
    @Json(name = "wind")
    WIND(
        titleRes = R.string.measurement_wind,
        formatRes = R.string.measurement_value_wind,
        iicon = WeatherIcons.Icon.wic_strong_wind
    )
}
