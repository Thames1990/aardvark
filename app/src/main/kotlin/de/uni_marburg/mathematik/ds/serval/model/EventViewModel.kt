package de.uni_marburg.mathematik.ds.serval.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: EventDao = EventDatabase.get(application).eventDao()

    val pagedList: LiveData<PagedList<Event>> = LivePagedListBuilder(
        dao.getAllPaged(),
        PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
    ).build()

    val liveData: LiveData<List<Event>>
        get() = dao.getAllLive()

    operator fun get(id: String) = dao.getById(id)

    fun getFromRepository(deleteEvents: Boolean = false) {
        val events: List<Event> = EventRepository.fetch()
        if (deleteEvents) dao.removeAll()
        dao.insertOrUpdate(events)
    }

    fun sortBy(eventComparator: EventComparator, descending: Boolean = false): Boolean {
        val events: List<Event> = dao.getAll()
        val sortedEvents: List<Event> = eventComparator.sort(events, descending)
        dao.insertOrUpdate(sortedEvents)
        return true
    }

    companion object {
        private const val PAGE_SIZE = 100
    }

}

sealed class EventComparator {

    object Distance : EventComparator() {
        override fun sort(events: List<Event>, descending: Boolean): List<Event> =
            if (descending) events.sortedByDescending { event ->
                event.location.distanceTo(MainActivity.lastLocation)
            }
            else events.sortedBy { event -> event.location.distanceTo(MainActivity.lastLocation) }
    }

    object Measurements : EventComparator() {
        override fun sort(events: List<Event>, descending: Boolean): List<Event> =
            if (descending) events.sortedByDescending { event -> event.measurements.size }
            else events.sortedBy { event -> event.measurements.size }
    }

    object Time : EventComparator() {
        override fun sort(events: List<Event>, descending: Boolean): List<Event> =
            if (descending) events.sortedBy { event -> event.time }
            else events.sortedByDescending { event -> event.time }
    }

    abstract fun sort(events: List<Event>, descending: Boolean): List<Event>

}