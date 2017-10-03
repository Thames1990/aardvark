package de.uni_marburg.mathematik.ds.serval.model

import android.content.Context
import android.content.res.Resources
import android.location.Location
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem
import com.squareup.moshi.Json
import de.uni_marburg.mathematik.ds.serval.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

data class Event(
        val time: Long,
        @Json(name = "location")
        private val geohashLocation: GeohashLocation,
        @Json(name = "measurements")
        val measurements: List<Measurement>
) : ClusterItem, Parcelable {

    val location: Location
        get() {
            val location = Location("")
            location.latitude = this.geohashLocation.latitude
            location.longitude = this.geohashLocation.longitude
            return location
        }

    override fun getPosition(): LatLng {
        val location = location
        return LatLng(location.latitude, location.longitude)
    }

    override fun getSnippet(): String {
        val format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.SHORT,
                Locale.getDefault()
        )
        return format.format(time)
    }

    override fun getTitle(): String = "Event"

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { Event(it) }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readLong(),
            parcelIn.readValue(GeohashLocation::class.java.classLoader) as GeohashLocation,
            mutableListOf<Measurement>().apply {
                parcelIn.readTypedList(this, Measurement.CREATOR)
            }
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(time)
        parcel.writeValue(geohashLocation)
        parcel.writeTypedList(measurements)
    }

    override fun describeContents(): Int = 0
}

data class GeohashLocation(
        val latitude: Double,
        val longitude: Double,
        private val geohash: String
) : Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { GeohashLocation(it) }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readDouble(),
            parcelIn.readDouble(),
            parcelIn.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
        parcel.writeString(geohash)
    }

    override fun describeContents(): Int = 0
}

data class Measurement(val type: MeasurementType, val value: Int) : Parcelable {

    companion object {
        @JvmField
        @Suppress("unused")
        val CREATOR = createParcel { Measurement(it) }
    }

    constructor(parcelIn: Parcel) : this(
            parcelIn.readValue(MeasurementType::class.java.classLoader) as MeasurementType,
            parcelIn.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(type)
        parcel.writeInt(value)
    }

    override fun describeContents(): Int = 0
}

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
                    context.getString(R.string.exception_measurement_type_without_icon),
                    toString()
            ))
        }
        return resId
    }

    override fun toString(): String = name[0] + name.substring(1).toLowerCase()
}

inline fun <reified T : Parcelable> createParcel(
        crossinline createFromParcel: (Parcel) -> T?): Parcelable.Creator<T> =
        object : Parcelable.Creator<T> {
            override fun createFromParcel(source: Parcel): T? = createFromParcel(source)
            override fun newArray(size: Int): Array<out T?> = arrayOfNulls(size)
        }
