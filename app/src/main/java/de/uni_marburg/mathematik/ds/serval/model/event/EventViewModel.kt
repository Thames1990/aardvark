package de.uni_marburg.mathematik.ds.serval.model.event

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity

class EventViewModel(application: Application) : AndroidViewModel(application) {

    val dao: EventDao by lazy {
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
        // TODO Figure out why sorting isn't working with events loaded from the DAO
        val events: List<Event> = EventRepository.fetch()
        dao.deleteAll()

        val sortedEvents: List<Event> = when (eventComparator) {
            EventComparator.DISTANCE -> events.sortedBy { event ->
                if (reversed) -event.location.distanceTo(MainActivity.lastLocation)
                else event.location.distanceTo(MainActivity.lastLocation)
            }
            EventComparator.MEASUREMENTS -> events.sortedBy { event ->
                if (reversed) -event.measurements.size
                else event.measurements.size
            }
            EventComparator.TIME -> events.sortedBy { event ->
                if (reversed) -event.time
                else event.time
            }
        }

        dao.insert(sortedEvents)
    }

    fun reload() = ioThread {
        dao.deleteAll()
        dao.insert(EventRepository.fetch())
    }

    companion object {
        private const val PAGE_SIZE = 100
        private const val ENABLE_PLACEHOLDERS = true
    }
}

enum class EventComparator {
    DISTANCE, MEASUREMENTS, TIME
}