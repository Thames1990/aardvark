package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;

/**
 * Created by thames1990 on 08.09.17.
 */
public abstract class EventFragment<T extends Event> extends BaseFragment {
    
    static final int EVENT_COUNT = 50;
    
    List<T> events;
    
    EventCallback<T> eventCallback;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //noinspection unchecked
        eventCallback = (EventCallback<T>) getActivity();
    }
    
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        requestEvents(EventComparator.DISTANCE, false, EVENT_COUNT);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    
    protected void requestEvents(EventComparator comparator, boolean reversed, int count) {
        events = eventCallback.onEventsRequested(comparator, reversed, count);
    }
}
