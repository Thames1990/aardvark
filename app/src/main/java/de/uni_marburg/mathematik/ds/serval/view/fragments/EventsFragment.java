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
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.GenericEventAdapter;
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.util.GridSpacingItemDecoration;
import de.uni_marburg.mathematik.ds.serval.view.util.SwipeToDeleteItemDecoration;
import de.uni_marburg.mathematik.ds.serval.view.util.SwipeToDeleteTouchHelper;

/**
 * Created by thames1990 on 28.08.17.
 */
public class EventsFragment<T extends Event> extends Fragment {
    
    public static final String EVENTS = "EVENTS";
    
    private List<T> events;
    
    private GenericEventAdapter adapter;
    
    private Unbinder unbinder;
    
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    
    public static <T extends Event> EventsFragment newInstance(ArrayList<T> events) {
        EventsFragment fragment = new EventsFragment<>();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS, events);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!getArguments().containsKey(EVENTS)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    EVENTS
            ));
        }
        events = getArguments().getParcelableArrayList(EVENTS);
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
    
    private void setupRecyclerView() {
        setupLayoutManager();
        setupAdapter();
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
                    new ItemTouchHelper(new SwipeToDeleteTouchHelper(getContext()));
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
    
    private void setupAdapter() {
        // TODO Find a better way
        if (events.get(0) instanceof GenericEvent) {
            //noinspection unchecked
            adapter = new GenericEventAdapter((List<GenericEvent>) events);
        }
        recyclerView.setAdapter(adapter);
    }
}
