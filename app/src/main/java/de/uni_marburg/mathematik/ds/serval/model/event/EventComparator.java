package de.uni_marburg.mathematik.ds.serval.model.event;

/**
 * Is used to to indicate which {@link java.util.Comparator comparator} should be used to sort
 * {@link Event events}.
 */
public enum EventComparator {
    /**
     * Sort {@link Event events} by their timestamp.
     */
    TIME,
    /**
     * Sort {@link Event events} by their distance to the current users location.
     */
    DISTANCE
}
