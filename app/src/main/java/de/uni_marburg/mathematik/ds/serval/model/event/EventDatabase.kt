package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import com.squareup.moshi.JsonAdapter
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

    private val dataAdapter: JsonAdapter<Data> = EventRepository.moshi.adapter(Data::class.java)

    @TypeConverter
    fun fromDataJson(json: String): Data? = dataAdapter.fromJson(json)

    @TypeConverter
    fun fromData(data: Data): String = dataAdapter.toJson(data)
}

private val ioExecutor = Executors.newSingleThreadExecutor()

fun ioThread(f: () -> Unit) = ioExecutor.execute(f)