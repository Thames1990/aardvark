package de.uni_marburg.mathematik.ds.serval.model

sealed class EventComparator {
    object Distance : EventComparator()
    object Measurement : EventComparator()
    object Time : EventComparator()
}
