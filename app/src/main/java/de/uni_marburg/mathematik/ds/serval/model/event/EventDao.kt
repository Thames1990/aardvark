package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.paging.DataSource
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

@Dao
interface EventDao {

    @Query("SELECT COUNT(*) FROM events")
    fun count(): Int

    @Query("SELECT * FROM events WHERE id = :id")
    fun getById(id: Long): Event

    @Query("SELECT * FROM events")
    fun getAll(): List<Event>

    @Query("SELECT * FROM events")
    fun getAllPaged(): DataSource.Factory<Int, Event>

    @Insert
    fun insert(event: Event)

    @Insert
    fun insert(vararg events: Event)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(events: List<Event>)

    @Query("DELETE FROM events")
    fun deleteAll()
}