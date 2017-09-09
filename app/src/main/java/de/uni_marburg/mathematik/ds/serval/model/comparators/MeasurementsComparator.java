package de.uni_marburg.mathematik.ds.serval.model.comparators;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Created by thames1990 on 10.09.17.
 */
public class MeasurementsComparator<T extends Event> implements Comparator<T> {
    
    @Override
    public int compare(T t, T t1) {
        Integer tSize = t.getMeasurements().size();
        Integer t1size = t1.getMeasurements().size();
        return tSize.compareTo(t1size);
    }
}
