package de.uni_marburg.mathematik.ds.serval.model.event;

import android.location.Location;

import de.uni_marburg.mathematik.ds.serval.controller.adapters.EventAdapter;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;

/**
 * Is used to to indicate which {@link java.util.Comparator comparator} should be used to sort
 * {@link Event events}.
 */
public enum EventComparator {
    /**
     * Sort {@link Event events} based on their their timestamp.
     */
    TIME,
    /**
     * Sort {@link Event events} based on their distance to the current users location.
     */
    DISTANCE,
    /**
     * Sort {@link Event events} based on their count of measurements.
     */
    MEASUREMENTS,
    /**
     * Just for testing. Shuffles the {@link Event events}.
     */
    SHUFFLE,
    /**
     * Don't sort the {@link Event events} at all. Is only useful in {@link
     * MainActivity#onEventsRequested(EventComparator, boolean, int)} and not in {@link
     * EventAdapter#filter(EventComparator, boolean, Location)}, because the list is either already
     * changed or wouldn't be changed at all. I'm not storing a copy of it.
     */
    NONE
}
