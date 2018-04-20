package de.uni_marburg.mathematik.ds.serval.model

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
abstract class EventDao {

    @Query("SELECT * FROM events WHERE id = :id")
    abstract fun getById(id: String): Event

    @Query("SELECT * FROM events")
    abstract fun getAll(): LiveData<List<Event>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertOrUpdate(events: List<Event>?)

    @Query("DELETE FROM events")
    abstract fun removeAll()

}