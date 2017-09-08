package de.uni_marburg.mathematik.ds.serval.interfaces;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Created by thames1990 on 08.09.17.
 */
public interface EventCallback<T extends Event> {
    
    void onEventsLoaded(List<T> events);
    
    List<T> onEventsRequested(Event.EventComparator comparator, int count);
    
}
