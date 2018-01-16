package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.squareup.moshi.Json
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "events")
data class Event(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val time: Long,
        @Json(name = "location") val geohashLocation: GeohashLocation,
        val measurements: List<Measurement>
) : ClusterItem {

    val location: Location
        get() = Location(BuildConfig.APPLICATION_ID).apply {
            latitude = geohashLocation.latitude
            longitude = geohashLocation.longitude
        }

    override fun getTitle(): String = javaClass.simpleName

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

data class GeohashLocation(val latitude: Double, val longitude: Double, val geohash: String)

data class Measurement(val type: MeasurementType, val value: Int)

// TODO Replace icon drawable with IIcon from Weather Icons
enum class MeasurementType(val textRes: Int, val formatRes: Int, val iconRes: Int) {
    @Json(name = "precipitation")
    PRECIPITATION(
            textRes = R.string.measurement_precipitation,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.precipitation
    ),

    @Json(name = "radiation")
    RADIATION(
            textRes = R.string.measurement_radiation,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.radiation
    ),
    @Json(name = "temperature")
    TEMPERATURE(
            textRes = R.string.measurement_temperature,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.temperature
    ),
    @Json(name = "wind")
    WIND(
            textRes = R.string.measurement_wind,
            formatRes = R.string.measurement_value_precipitation,
            iconRes = R.drawable.wind
    )
}
