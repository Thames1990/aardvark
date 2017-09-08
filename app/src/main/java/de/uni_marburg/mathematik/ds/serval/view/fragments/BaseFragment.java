package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;

/**
 * Created by thames1990 on 08.09.17.
 */
public abstract class BaseFragment<T extends Event> extends Fragment {
    
    static final int EVENT_COUNT = 50;
    
    List<T> events;
    
    EventCallback<T> eventCallback;
    
    private Unbinder unbinder;
    
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
        View view = inflater.inflate(getLayout(), container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Serval.getRefWatcher(getActivity()).watch(this);
    }
    
    protected abstract int getLayout();
    
    protected void requestEvents(EventComparator comparator, boolean reversed, int count) {
        events = eventCallback.onEventsRequested(comparator, reversed, count);
    }
}
