package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Types
import java.util.concurrent.Executors

@Database(entities = [Event::class], version = 1, exportSchema = false)
@TypeConverters(EventConverters::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun eventDao(): EventDao

    companion object {
        private var instance: EventDatabase? = null

        @Synchronized
        fun get(context: Context): EventDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    EventDatabase::class.java,
                    BuildConfig.APPLICATION_ID
                ).addCallback(object : RoomDatabase.Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) = ioThread {
                        get(context).eventDao().insert(EventRepository.fetch())
                    }
                }).build()
            }
            return instance!!
        }
    }

}

class EventConverters {

    private val listType = Types.newParameterizedType(List::class.java, Measurement::class.java)
    private val measurementsAdapter: JsonAdapter<List<Measurement>> =
        EventRepository.moshi.adapter(listType)

    private val geohashLocationAdapter: JsonAdapter<GeohashLocation> =
        EventRepository.moshi.adapter(GeohashLocation::class.java)

    @TypeConverter
    fun fromMeasurementJson(json: String): List<Measurement> =
        measurementsAdapter.fromJson(json) ?: emptyList()

    @TypeConverter
    fun fromMeasurementList(list: List<Measurement>): String = measurementsAdapter.toJson(list)

    @TypeConverter
    fun fromGeohashLocationJson(json: String): GeohashLocation =
        geohashLocationAdapter.fromJson(json) ?: GeohashLocation(
            latitude = 0.0,
            longitude = 0.0,
            geohash = ""
        )

    @TypeConverter
    fun fromGeohashLocationObject(geohashLocation: GeohashLocation): String =
        geohashLocationAdapter.toJson(geohashLocation)
}

private val ioExecutor = Executors.newSingleThreadExecutor()

fun ioThread(f: () -> Unit) = ioExecutor.execute(f)