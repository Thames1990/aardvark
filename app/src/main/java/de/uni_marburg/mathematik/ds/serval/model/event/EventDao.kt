package de.uni_marburg.mathematik.ds.serval.model.event

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

/**
 * Created by thames1990 on 10.01.18.
 */
@Dao
interface EventDao {

    @Query("SELECT * FROM events")
    fun getAllEvents(): List<Event>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvents(event: List<Event>)
}