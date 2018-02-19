package de.uni_marburg.mathematik.ds.serval.model.event

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
import net.sharewire.googlemapsclustering.ClusterItem
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "events")
data class Event(
    @PrimaryKey val id: String,
    val data: Data
) : ClusterItem {

    inline val time: Long
        get() = data.time

    inline val location: Location
        get() = Location(BuildConfig.APPLICATION_ID).apply {
            latitude = data.geohashLocation.latitude
            longitude = data.geohashLocation.longitude
        }

    inline val position: LatLng
        get() = LatLng(location.latitude, location.longitude)

    inline val measurements: List<Measurement>
        get() = data.measurements

    override fun getSnippet(): String? {
        val format = SimpleDateFormat.getDateTimeInstance(
            DateFormat.LONG,
            DateFormat.SHORT,
            Locale.getDefault()
        )
        return format.format(time)
    }

    override fun getLongitude(): Double = data.geohashLocation.longitude

    override fun getLatitude(): Double = data.geohashLocation.latitude

    override fun getTitle(): String? = javaClass.simpleName

}

data class Data(
    val time: Long,
    @Json(name = "location") val geohashLocation: GeohashLocation,
    val measurements: List<Measurement>
)


data class GeohashLocation(val latitude: Double, val longitude: Double, val geohash: String)

data class Measurement(val type: MeasurementType, val value: Int)

enum class MeasurementType(
    @StringRes val titleRes: Int,
    @StringRes val formatRes: Int,
    val iicon: IIcon
) {
    @Json(name = "precipitation")
    PRECIPITATION(
        titleRes = R.string.measurement_precipitation,
        formatRes = R.string.measurement_value_precipitation,
        iicon = WeatherIcons.Icon.wic_rain
    ),

    @Json(name = "radiation")
    RADIATION(
        titleRes = R.string.measurement_radiation,
        formatRes = R.string.measurement_value_precipitation,
        iicon = CommunityMaterial.Icon.cmd_atom
    ),

    @Json(name = "temperature")
    TEMPERATURE(
        titleRes = R.string.measurement_temperature,
        formatRes = R.string.measurement_value_precipitation,
        iicon = WeatherIcons.Icon.wic_thermometer
    ),

    @Json(name = "wind")
    WIND(
        titleRes = R.string.measurement_wind,
        formatRes = R.string.measurement_value_precipitation,
        iicon = WeatherIcons.Icon.wic_strong_wind
    )
}
