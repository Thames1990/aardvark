package de.uni_marburg.mathematik.ds.serval.model.comparators;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Compares {@link Event events} based on their {@link Event#measurements measurements}.
 *
 * @param <T> Type of the {@link Event event}
 */
public class MeasurementsComparator<T extends Event> implements Comparator<T> {
    
    @Override
    public int compare(T t, T t1) {
        Integer tSize = t.getMeasurements().size();
        Integer t1size = t1.getMeasurements().size();
        return tSize.compareTo(t1size);
    }
}
