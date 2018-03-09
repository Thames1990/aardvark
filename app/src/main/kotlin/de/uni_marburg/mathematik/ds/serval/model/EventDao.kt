package de.uni_marburg.mathematik.ds.serval.model

import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import org.intellij.lang.annotations.Language

@Dao
abstract class EventDao {

    @Language("RoomSql")
    @Query("SELECT COUNT(*) FROM events")
    abstract fun count(): Int

    @Language("RoomSql")
    @Query("SELECT * FROM events WHERE id = :id")
    abstract fun getById(id: String): Event

    @Language("RoomSql")
    @Query("SELECT * FROM events")
    abstract fun getAll(): List<Event>

    @Language("RoomSql")
    @Query("SELECT * FROM events")
    abstract fun getAllPaged(): DataSource.Factory<Int, Event>

    @Query("SELECT * FROM events")
    abstract fun getAllLive(): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrUpdate(events: List<Event>)

}