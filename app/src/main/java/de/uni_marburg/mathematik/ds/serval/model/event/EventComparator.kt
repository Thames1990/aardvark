package de.uni_marburg.mathematik.ds.serval.model.event

/** Defines options to compare [events][Event] */
sealed class EventComparator {
    /** Defines options to compare events by the distance to the current location of the device **/
    object Distance : EventComparator()

    /** Defines options to compare events by their amount of measurements **/
    object Measurement : EventComparator()

    /** Defines options to compare events by their occurence time **/
    object Time : EventComparator()
}
