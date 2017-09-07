package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;

/**
 * Created by thames1990 on 28.08.17.
 */
public class DashboardFragment<T extends Event> extends Fragment {
    
    private static final String EVENTS = "EVENTS";
    
    private static final int EVENT_COUNT = 50;
    
    private ArrayList<T> events;
    
    private Unbinder unbinder;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            //noinspection unchecked
            events = ((MainActivity) getActivity()).getEvents(EVENT_COUNT);
        } else {
            events = savedInstanceState.getParcelableArrayList(EVENTS);
        }
    }
    
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
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
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList(EVENTS, events);
    }
}
