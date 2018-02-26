package de.uni_marburg.mathematik.ds.serval.model.event

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.DataSource
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity

class EventViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        private const val PAGE_SIZE = 100
        private const val ENABLE_PLACEHOLDERS = true
    }

    private val dao: EventDao = EventDatabase.get(application).eventDao()

    private val dataSourceFactory: DataSource.Factory<Int, Event> = dao.getAllPaged()

    private val configBuilder: PagedList.Config.Builder = PagedList.Config.Builder()
        .setPageSize(PAGE_SIZE)
        .setEnablePlaceholders(ENABLE_PLACEHOLDERS)

    private val config = configBuilder.build()

    private val livePagedListBuilder: LivePagedListBuilder<Int, Event> =
        LivePagedListBuilder(dataSourceFactory, config)

    val events: LiveData<PagedList<Event>> = livePagedListBuilder.build()

    fun count(): Int = dao.count()

    fun getAll() = dao.getAll()

    fun reload() {
        dao.deleteAll()
        val events: List<Event> = EventRepository.fetch()
        dao.insert(events)
    }

    fun sort(eventComparator: EventComparator, reversed: Boolean = false) = ioThread {
        val events: List<Event> = dao.getAll()
        val sortedEvents: List<Event> = eventComparator.sort(events, reversed)
        dao.insert(sortedEvents)
    }

}

sealed class EventComparator {

    object Distance : EventComparator() {
        override fun sort(events: List<Event>, reversed: Boolean): List<Event> =
            if (reversed) events.sortedByDescending { event ->
                event.location.distanceTo(MainActivity.lastLocation)
            }
            else events.sortedBy { event -> event.location.distanceTo(MainActivity.lastLocation) }
    }

    object Measurements : EventComparator() {
        override fun sort(events: List<Event>, reversed: Boolean): List<Event> =
            if (reversed) events.sortedByDescending { event -> event.measurements.size }
            else events.sortedBy { event -> event.measurements.size }
    }

    object Time : EventComparator() {
        override fun sort(events: List<Event>, reversed: Boolean): List<Event> =
            if (reversed) events.sortedBy { event -> event.time }
            else events.sortedByDescending { event -> event.time }
    }

    abstract fun sort(events: List<Event>, reversed: Boolean): List<Event>

}