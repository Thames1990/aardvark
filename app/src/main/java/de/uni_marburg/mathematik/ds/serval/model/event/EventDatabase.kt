package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.room.*
import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.utils.SingletonHolder

/**
 * Created by thames1990 on 10.01.18.
 */
@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(EventConverters::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object : SingletonHolder<EventDatabase, Context>({
        Room.databaseBuilder(
                it.applicationContext,
                EventDatabase::class.java,
                BuildConfig.APPLICATION_ID
        ).build()
    })

}

class EventConverters {

    private val listType = Types.newParameterizedType(List::class.java, Measurement::class.java)
    private val measurementsAdapter: JsonAdapter<List<Measurement>> =
            EventRepository.moshi.adapter(listType)

    private val geohashLocationAdapter: JsonAdapter<GeohashLocation> =
            EventRepository.moshi.adapter(GeohashLocation::class.java)

    @TypeConverter
    fun fromMeasurementJson(json: String): List<Measurement> {
        return measurementsAdapter.fromJson(json) ?: emptyList()
    }

    @TypeConverter
    fun fromMeasurementList(list: List<Measurement>): String {
        return measurementsAdapter.toJson(list)
    }

    @TypeConverter
    fun fromGeohashLocationJson(json: String): GeohashLocation {
        return geohashLocationAdapter.fromJson(json) ?: GeohashLocation(
                latitude = 0.0,
                longitude = 0.0,
                geohash = ""
        )
    }

    @TypeConverter
    fun fromGeohashLocationObject(geohashLocation: GeohashLocation): String {
        return geohashLocationAdapter.toJson(geohashLocation)
    }
}