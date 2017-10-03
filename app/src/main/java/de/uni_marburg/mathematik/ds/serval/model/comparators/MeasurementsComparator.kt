package de.uni_marburg.mathematik.ds.serval.model.comparators

import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement
import java.util.*

/** Compares [events][Event] based on their amount of [measurements][Measurement]. **/
class MeasurementsComparator : Comparator<Event> {

    override fun compare(event1: Event, event2: Event): Int =
            compareValues(event1.measurements.size, event2.measurements.size)
}
