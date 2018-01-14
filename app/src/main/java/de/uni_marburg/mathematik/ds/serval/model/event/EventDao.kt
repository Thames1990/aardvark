package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface EventDao {

    @Query("SELECT COUNT(*) FROM events")
    fun count(): Int

    @Query("SELECT * FROM events")
    fun getAll(): List<Event>

    @Insert
    fun insert(event: Event)

    @Insert
    fun insertAll(vararg events: Event)

    @Insert
    fun insertAll(events: List<Event>)
}