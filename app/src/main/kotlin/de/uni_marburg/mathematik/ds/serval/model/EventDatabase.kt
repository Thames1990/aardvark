package de.uni_marburg.mathematik.ds.serval.model

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.room.*
import android.content.Context
import ca.allanwang.kau.utils.isNetworkAvailable
import com.squareup.moshi.JsonAdapter
import java.util.concurrent.Executors

@Database(entities = [Event::class], version = 2, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun dao(): EventDao

    companion object {
        private var database: EventDatabase? = null

        @Synchronized
        fun get(context: Context): EventDatabase = database ?: build(context).also { database = it }

        private fun build(context: Context) = Room.databaseBuilder(
            context.applicationContext,
            EventDatabase::class.java,
            "events.db"
        ).addCallback(object : RoomDatabase.Callback() {
            private val ioExecutor = Executors.newSingleThreadExecutor()
            private fun ioThread(f: () -> Unit) = ioExecutor.execute(f)

            override fun onCreate(db: SupportSQLiteDatabase) = ioThread {
                if (context.isNetworkAvailable) {
                    val events: List<Event> = EventRepository.fetch()
                    val eventDatabase: EventDatabase = get(context)
                    val dao: EventDao = eventDatabase.dao()
                    dao.insertOrUpdate(events)
                }
            }
        }).build()
    }

}

private class DataConverter {

    private val adapter: JsonAdapter<Data> = EventRepository.moshi.adapter(Data::class.java)

    @TypeConverter
    fun toData(json: String): Data? = adapter.fromJson(json)

    @TypeConverter
    fun fromData(data: Data): String = adapter.toJson(data)

}