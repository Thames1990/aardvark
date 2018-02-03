package de.uni_marburg.mathematik.ds.serval.model.event

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: EventDao by lazy {
        EventDatabase.get(application).eventDao()
    }

    val events: LiveData<PagedList<Event>> by lazy {
        LivePagedListBuilder(
            dao.getAllPaged(),
            PagedList.Config.Builder()
                .setPageSize(PAGE_SIZE)
                .setEnablePlaceholders(ENABLE_PLACEHOLDERS)
                .build()
        ).build()
    }

    fun sort(eventComparator: EventComparator, reversed: Boolean = false) = ioThread {
        val events: List<Event> = EventRepository.fetch()
        dao.deleteAll()
        val sortedEvents: List<Event> = eventComparator.sort(events, reversed)
        dao.insert(sortedEvents)
    }

    fun count(): Int = dao.count()

    fun getAll() = dao.getAll()

    fun reload() = ioThread {
        dao.deleteAll()
        dao.insert(EventRepository.fetch())
    }

    companion object {
        private const val PAGE_SIZE = 100
        private const val ENABLE_PLACEHOLDERS = true
    }
}

sealed class EventComparator {

    object Distance : EventComparator() {
        override fun sort(events: List<Event>, reversed: Boolean): List<Event> =
            events.sortedBy { event ->
                if (reversed) -event.location.distanceTo(MainActivity.lastLocation)
                else event.location.distanceTo(MainActivity.lastLocation)
            }
    }

    object Measurements : EventComparator() {
        override fun sort(events: List<Event>, reversed: Boolean): List<Event> =
            events.sortedBy { event ->
                if (reversed) -event.measurements.size
                else event.measurements.size
            }
    }

    object Time : EventComparator() {
        override fun sort(events: List<Event>, reversed: Boolean): List<Event> =
            events.sortedBy { event ->
                if (reversed) -event.time
                else event.time
            }
    }

    abstract fun sort(events: List<Event>, reversed: Boolean): List<Event>
}