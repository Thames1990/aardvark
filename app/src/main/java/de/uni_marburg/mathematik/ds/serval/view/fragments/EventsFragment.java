package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.GenericEventAdapter;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;
import de.uni_marburg.mathematik.ds.serval.view.item_decorations.GridSpacingItemDecoration;
import de.uni_marburg.mathematik.ds.serval.view.item_decorations.SwipeToDeleteItemDecoration;
import de.uni_marburg.mathematik.ds.serval.view.item_touch_helpers.SwipeToDeleteItemTouchHelper;

/**
 * Created by thames1990 on 28.08.17.
 */
public class EventsFragment extends BaseFragment {
    
    private GenericEventAdapter adapter;
    
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        requestEvents(EventComparator.DISTANCE, false, EVENT_COUNT);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
    }
    
    @Override
    protected int getLayout() {
        return R.layout.fragment_events;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_filter, menu);
        PrefManager prefManager = new PrefManager(getContext());
        // Hide location filter, if the user denied location update permission
        if (!prefManager.requestLocationUpdates()) {
            menu.findItem(R.id.action_filter_events_distance).setVisible(false);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_events_distance_ascending:
                adapter.filter(
                        EventComparator.DISTANCE,
                        false,
                        ((MainActivity) getActivity()).getLastLocation()
                );
                return true;
            case R.id.action_filter_events_distance_descending:
                adapter.filter(
                        EventComparator.DISTANCE,
                        true,
                        ((MainActivity) getActivity()).getLastLocation()
                );
                return true;
            case R.id.action_filter_events_time_ascending:
                adapter.filter(EventComparator.TIME, false, null);
                return true;
            case R.id.action_filter_events_time_descending:
                adapter.filter(EventComparator.TIME, true, null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    private void setupRecyclerView() {
        setupLayoutManager();
        //noinspection unchecked
        adapter = new GenericEventAdapter(events);
        recyclerView.setAdapter(adapter);
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
                    new ItemTouchHelper(new SwipeToDeleteItemTouchHelper<>(getContext()));
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
