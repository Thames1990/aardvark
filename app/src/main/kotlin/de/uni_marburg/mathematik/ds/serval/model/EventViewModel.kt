package de.uni_marburg.mathematik.ds.serval.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.ASCENDING
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.DESCENDING
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class EventViewModel(application: Application) : AndroidViewModel(application) {

    val dao: EventDao = EventDatabase.get(application).dao()

    val events: LiveData<List<Event>>
        get() = dao.getAll()

    operator fun get(id: String) = dao.getById(id)

    inline fun getFromRepository(
        deleteEvents: Boolean = false,
        crossinline doOnFinish: () -> Unit = {}
    ) = doAsync {
        val events: List<Event> = EventRepository.fetch()
        if (deleteEvents) dao.removeAll()
        dao.insertOrUpdate(events)
        uiThread { doOnFinish() }
    }

    inline fun sortBy(
        comparator: EventComparator,
        order: EventComparator.Order = ASCENDING,
        crossinline doOnFinish: () -> Unit = {}
    ) = doAsync {
        val events: List<Event>? = dao.getAll().value
        val sortedEvents: List<Event>? = comparator.sort(events, order)
        dao.insertOrUpdate(sortedEvents)
        uiThread { doOnFinish() }
    }

}

sealed class EventComparator {

    enum class Order {
        ASCENDING, DESCENDING
    }

    object Distance : EventComparator() {
        override fun sort(events: List<Event>?, order: Order): List<Event>? = when (order) {
            ASCENDING -> events?.sortedBy { event ->
                event.location.distanceTo(MainActivity.deviceLocation)
            }
            DESCENDING -> events?.sortedByDescending { event ->
                event.location.distanceTo(MainActivity.deviceLocation)
            }
        }
    }

    object Measurements : EventComparator() {
        override fun sort(events: List<Event>?, order: Order): List<Event>? = when (order) {
            ASCENDING -> events?.sortedBy { event -> event.measurements.size }
            DESCENDING -> events?.sortedByDescending { event -> event.measurements.size }
        }
    }

    object Time : EventComparator() {
        override fun sort(events: List<Event>?, order: Order): List<Event>? = when (order) {
            ASCENDING -> events?.sortedByDescending { event -> event.time }
            DESCENDING -> events?.sortedBy { event -> event.time }
        }
    }

    abstract fun sort(events: List<Event>?, order: Order): List<Event>?

}