package de.uni_marburg.mathematik.ds.serval.model.util;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Created by thames1990 on 08.09.17.
 */
public class TimeComparator<T extends Event> implements Comparator<T> {
    
    @Override
    public int compare(T t, T t1) {
        return t.getTime().compareTo(t1.getTime());
    }
}
