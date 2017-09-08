package de.uni_marburg.mathematik.ds.serval.model.comparators;

import android.location.Location;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Compares {@link Event events} based on their location.
 */
public class LocationComparator<T extends Event> implements Comparator<T> {
    
    /**
     * The location to calculate the distance to. This is the current users location.
     */
    private Location origin;
    
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
