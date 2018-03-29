package de.uni_marburg.mathematik.ds.serval.model

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverter
import android.arch.persistence.room.TypeConverters
import android.content.Context
import ca.allanwang.kau.utils.isNetworkAvailable
import com.squareup.moshi.JsonAdapter
import de.uni_marburg.mathematik.ds.serval.utils.roomDb

@Database(entities = [Event::class], version = 2, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class EventDatabase : RoomDatabase() {

    abstract fun dao(): EventDao

    companion object {
        private var database: EventDatabase? = null

        @Synchronized
        fun get(context: Context): EventDatabase =
            database ?: context.applicationContext.roomDb<EventDatabase>(
                name = "events.db",
                onFirstCreate = {
                    if (context.isNetworkAvailable) {
                        val events: List<Event> = EventRepository.fetch()
                        val eventDatabase: EventDatabase = get(context)
                        val dao: EventDao = eventDatabase.dao()
                        dao.insertOrUpdate(events)
                    }
                }
            ).also { database = it }
    }

}

private class DataConverter {

    private val adapter: JsonAdapter<Data> = EventRepository.moshi.adapter(Data::class.java)

    @TypeConverter
    fun toData(json: String): Data? = adapter.fromJson(json)

    @TypeConverter
    fun fromData(data: Data): String = adapter.toJson(data)

}