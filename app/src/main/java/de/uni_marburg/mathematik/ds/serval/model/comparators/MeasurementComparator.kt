package de.uni_marburg.mathematik.ds.serval.model.comparators

import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement
import java.util.*

/** Compares [events][Event] based on their [measurements][Measurement]. **/
class MeasurementComparator : Comparator<Measurement> {

    override fun compare(measurement: Measurement, t1: Measurement): Int {
        return if (measurement.type == t1.type) {
            measurement.type.compareTo(measurement.type)
        } else measurement.value.compareTo(t1.value)
    }
}
