package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import java.util.concurrent.Executors

@Database(entities = [Event::class], version = 2, exportSchema = false)
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

    private val moshi: Moshi = EventRepository.moshi

    private val dataAdapter: JsonAdapter<Data> = moshi.adapter(Data::class.java)

    private val type = Types.newParameterizedType(List::class.java, Measurement::class.java)
    private val measurementsAdapter: JsonAdapter<List<Measurement>> = moshi.adapter(type)

    private val geohashLocationAdapter: JsonAdapter<GeohashLocation> =
        moshi.adapter(GeohashLocation::class.java)

    @TypeConverter
    fun fromDataJson(json: String): Data? = dataAdapter.fromJson(json)

    @TypeConverter
    fun fromData(data: Data): String = dataAdapter.toJson(data)

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
    fun fromGeohashLocation(geohashLocation: GeohashLocation): String =
        geohashLocationAdapter.toJson(geohashLocation)
}

private val ioExecutor = Executors.newSingleThreadExecutor()

fun ioThread(f: () -> Unit) = ioExecutor.execute(f)