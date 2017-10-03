package de.uni_marburg.mathematik.ds.serval.model.comparators

import de.uni_marburg.mathematik.ds.serval.model.event.Event
import java.util.*

/** Compares [events][Event] based on their occurrence time. **/
class TimeComparator : Comparator<Event> {

    override fun compare(event1: Event, event2: Event): Int =
            compareValues(event1.time, event2.time)

}
