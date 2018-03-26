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
import de.uni_marburg.mathematik.ds.serval.enums.PrecipitationUnits
import de.uni_marburg.mathematik.ds.serval.enums.RadiationUnits
import de.uni_marburg.mathematik.ds.serval.enums.TemperatureUnits
import de.uni_marburg.mathematik.ds.serval.enums.WindUnits
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.settings.EventPrefs
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInSeconds
import net.sharewire.googlemapsclustering.ClusterItem

/**
 * An event is something which happens or takes place. An event is brief, possibly extremely brief.
 *
 * @property id Identifier, created from a SHA3-256 hash of [data]
 * @property data Describes this event
 */
@Entity(tableName = "events")
data class Event(@PrimaryKey val id: String, val data: Data) : ClusterItem {

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
        get() = LatLng(latitude, longitude)

    /**
     * Measurements captured by this event
     */
    inline val measurements: List<Measurement>
        get() = data.measurements

    /**
     * Seconds passed since this event occurred
     */
    inline val passedSeconds: Long
        get() = currentTimeInSeconds - time

    override fun getSnippet(): String? =
        AppearancePrefs.DateTimeFormat.format.formatTime(time * 1000)

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
 * @property convertedValue Measurement value converted to its type and user preference
 */
data class Measurement(val type: MeasurementType, private val value: Double) {

    val convertedValue: Double
        get() = when (type) {
            MeasurementType.PRECIPITATION -> precipitationValue
            MeasurementType.RADIATION -> radiationValue
            MeasurementType.TEMPERATURE -> temperatureValue
            MeasurementType.WIND -> windValue
        }

    private inline val precipitationValue: Double
        get() = when (EventPrefs.PrecipitationUnit.unit) {
            PrecipitationUnits.MILLIMETER -> value
            PrecipitationUnits.INCHES -> value / 25.4
        }

    private inline val radiationValue: Double
        get() = when (EventPrefs.RadiationUnit.unit) {
            RadiationUnits.REM -> value / 10000
            RadiationUnits.MILLIREM -> value / 10
            RadiationUnits.MILLISIEVERT -> value / 1000
            RadiationUnits.SIEVERT -> value / 1000000
            RadiationUnits.MICROSIEVERT -> value
            RadiationUnits.BANANA_EQUIVALENT_DOSE -> value * 10
        }

    private inline val temperatureValue: Double
        get() = when (EventPrefs.TemperatureUnit.unit) {
            TemperatureUnits.CELSIUS -> value
            TemperatureUnits.FAHRENHEIT -> (value * 1.8 + 32)
            TemperatureUnits.KELVIN -> (value + 273.15)
        }

    private inline val windValue: Double
        get() = when (EventPrefs.WindUnit.unit) {
            WindUnits.METRES -> value
            WindUnits.MILES -> value * (25 / 11)
            WindUnits.KILOMETRES -> value * 3.6
            WindUnits.NAUTICAL_KNOTS -> value * 1.94
        }

}

/**
 * Type of a [measurement][Measurement].
 *
 * @property titleRes Resource ID of the title
 * @property unit Measurement type unit
 * @property iicon Measurement type icon
 */
enum class MeasurementType(
    @StringRes val titleRes: Int,
    @StringRes val unit: Int,
    val iicon: IIcon
) {

    /**
     * Any product of the condensation of atmospheric water vapor that falls under gravity.
     * The main forms of precipitation include drizzle, rain, sleet, snow, graupel and hail.
     */
    @Json(name = "precipitation")
    PRECIPITATION(
        titleRes = R.string.measurement_precipitation,
        unit = EventPrefs.PrecipitationUnit.unit.unitRes,
        iicon = WeatherIcons.Icon.wic_rain
    ),

    /**
     * The emission or transmission of energy in the form of waves or particles through space or
     * through a material medium.
     */
    @Json(name = "radiation")
    RADIATION(
        titleRes = R.string.measurement_radiation,
        unit = EventPrefs.RadiationUnit.unit.unitRes,
        iicon = CommunityMaterial.Icon.cmd_atom
    ),

    /**
     *  Physical quantity expressing hot and cold.
     */
    @Json(name = "temperature")
    TEMPERATURE(
        titleRes = R.string.measurement_temperature,
        unit = EventPrefs.TemperatureUnit.unit.unitRes,
        iicon = WeatherIcons.Icon.wic_thermometer
    ),

    /**
     * The flow of gases on a large scale. On the surface of the Earth, wind consists of the bulk
     * movement of air.
     */
    @Json(name = "wind")
    WIND(
        titleRes = R.string.measurement_wind,
        unit = EventPrefs.WindUnit.unit.unitRes,
        iicon = WeatherIcons.Icon.wic_strong_wind
    )
}
