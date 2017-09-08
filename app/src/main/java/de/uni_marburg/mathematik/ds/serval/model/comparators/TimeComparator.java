package de.uni_marburg.mathematik.ds.serval.model.comparators;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Compares {@link Event events} based on their time.
 */
public class TimeComparator<T extends Event> implements Comparator<T> {
    
    @Override
    public int compare(T t, T t1) {
        return t.getTime().compareTo(t1.getTime());
    }
}
