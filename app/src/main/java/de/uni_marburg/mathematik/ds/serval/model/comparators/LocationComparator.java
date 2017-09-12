package de.uni_marburg.mathematik.ds.serval.model.comparators;

import android.location.Location;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Compares {@link Event events} based on their {@link Event#location}.
 *
 * @param <T> Type of the {@link Event event}
 */
public class LocationComparator<T extends Event> implements Comparator<T> {
    
    /**
     * The location to calculate the distance to. This is usually the last known location.
     */
    private Location origin;
    
    /**
     * Creates a new location comparator.
     *
     * @param origin The location to calculate the distance to. This is usually the last known
     *               location.
     */
    public LocationComparator(Location origin) {
        this.origin = origin;
    }
    
    @Override
    public int compare(T t, T t1) {
        return Float.compare(
                origin.distanceTo(t.getLocation()),
                origin.distanceTo(t1.getLocation())
        );
    }
}
