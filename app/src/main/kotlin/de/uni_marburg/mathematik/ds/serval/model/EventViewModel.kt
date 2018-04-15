package de.uni_marburg.mathematik.ds.serval.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.paging.LivePagedListBuilder
import android.arch.paging.PagedList
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.ASCENDING
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.DESCENDING
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EventViewModel(application: Application) : AndroidViewModel(application) {

    private val dao: EventDao = EventDatabase.get(application).dao()

    val pagedList: LiveData<PagedList<Event>> = LivePagedListBuilder(
        dao.getAllPaged(),
        PagedList.Config.Builder()
            .setPageSize(PAGE_SIZE)
            .build()
    ).build()

    val liveData: LiveData<List<Event>>
        get() = dao.getAllLive()

    operator fun get(id: String) = dao.getById(id)

    fun getFromRepository(
        deleteEvents: Boolean = false,
        doOnFinish: () -> Unit = {}
    ) = doAsync {
        val events: List<Event> = EventRepository.fetch()
        if (deleteEvents) dao.removeAll()
        dao.insertOrUpdate(events)
        uiThread { doOnFinish() }
    }

    fun sortBy(
        comparator: EventComparator,
        order: EventComparator.Order = ASCENDING,
        doOnFinish: () -> Unit = {}
    ) = doAsync {
        val events: List<Event> = dao.getAll()
        val sortedEvents: List<Event> = comparator.sort(events, order)
        dao.insertOrUpdate(sortedEvents)
        uiThread { doOnFinish() }
    }

    companion object {
        private const val PAGE_SIZE = 100
    }

}

sealed class EventComparator {

    enum class Order {
        ASCENDING, DESCENDING
    }

    object Distance : EventComparator() {
        override fun sort(events: List<Event>, order: Order): List<Event> = when (order) {
            ASCENDING -> events.sortedBy { event ->
                event.location.distanceTo(MainActivity.deviceLocation)
            }
            DESCENDING -> events.sortedByDescending { event ->
                event.location.distanceTo(MainActivity.deviceLocation)
            }
        }
    }

    object Measurements : EventComparator() {
        override fun sort(events: List<Event>, order: Order): List<Event> = when (order) {
            ASCENDING -> events.sortedBy { event -> event.measurements.size }
            DESCENDING -> events.sortedByDescending { event -> event.measurements.size }
        }
    }

    object Time : EventComparator() {
        override fun sort(events: List<Event>, order: Order): List<Event> = when (order) {
            ASCENDING -> events.sortedByDescending { event -> event.time }
            DESCENDING -> events.sortedBy { event -> event.time }
        }
    }

    abstract fun sort(events: List<Event>, order: Order): List<Event>

}