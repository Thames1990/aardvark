package de.uni_marburg.mathematik.ds.serval.model.util;

import android.location.Location;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Created by thames1990 on 08.09.17.
 */
public class LocationComparator<T extends Event> implements Comparator<T> {
    
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
