package de.uni_marburg.mathematik.ds.serval.model.comparators

import android.location.Location
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import java.util.*

/** Compares [events][Event] based on their distance to an origin. **/
class LocationComparator(private val origin: Location?) : Comparator<Event> {

    override fun compare(event1: Event, event2: Event): Int =
            compareValues(origin?.distanceTo(event1.location), origin?.distanceTo(event2.location))

}
