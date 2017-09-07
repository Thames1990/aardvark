package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.GenericEventAdapter;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;
import de.uni_marburg.mathematik.ds.serval.view.util.GridSpacingItemDecoration;
import de.uni_marburg.mathematik.ds.serval.view.util.SwipeToDeleteItemDecoration;
import de.uni_marburg.mathematik.ds.serval.view.util.SwipeToDeleteTouchHelper;

/**
 * Created by thames1990 on 28.08.17.
 */
public class EventsFragment<T extends Event> extends Fragment {
    
    private static final String EVENTS = "EVENTS";
    
    private static final int EVENT_COUNT = 50;
    
    private ArrayList<T> events;
    
    private Unbinder unbinder;
    
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    
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
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        unbinder = ButterKnife.bind(this, view);
        setupRecyclerView();
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
    
    private void setupRecyclerView() {
        setupLayoutManager();
        //noinspection unchecked
        recyclerView.setAdapter(new GenericEventAdapter((List<GenericEvent>) events));
    }
    
    private void setupLayoutManager() {
        PrefManager prefManager = new PrefManager(getContext());
        if (prefManager.useLinearLayoutManger()) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.addItemDecoration(new DividerItemDecoration(
                    getContext(),
                    DividerItemDecoration.VERTICAL
            ));
            ItemTouchHelper touchHelper =
                    new ItemTouchHelper(new SwipeToDeleteTouchHelper<>(getContext()));
            touchHelper.attachToRecyclerView(recyclerView);
            recyclerView.addItemDecoration(new SwipeToDeleteItemDecoration());
        } else if (prefManager.useGridLayoutManger()) {
            recyclerView.setLayoutManager(new GridLayoutManager(
                    getContext(),
                    prefManager.getGridLayoutManagerSpanCount()
            ));
            recyclerView.addItemDecoration(new GridSpacingItemDecoration(
                    prefManager.getGridLayoutManagerSpanCount(),
                    ImageUtil.dpToPx(10, getContext()),
                    true
            ));
        } else if (prefManager.useStaggeredGridLayoutManger()) {
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(
                    prefManager.getStaggeredGridLayoutManagerSpanCount(),
                    StaggeredGridLayoutManager.VERTICAL
            ));
            // TODO Add item decoration
        }
    }
}
