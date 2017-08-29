package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.GenericEventAdapter;
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * Created by thames1990 on 28.08.17.
 */
public class EventsFragment<T extends Event> extends Fragment {

    public static final String EVENTS = "EVENTS";

    private List<T> events;

    private GenericEventAdapter adapter;

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
                    getString(R.string.fragment_must_contain_key_exception),
                    EVENTS
            ));
        }
        events = getArguments().getParcelableArrayList(EVENTS);
    }

    private void setupRecyclerView() {
//        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(
//                2,
//                ImageUtil.dpToPx(10, getContext()), true)
//        );
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        // TODO Find a better way
        if (events.get(0) instanceof GenericEvent) {
            //noinspection unchecked
            adapter = new GenericEventAdapter((List<GenericEvent>) events);
        }
        recyclerView.setAdapter(adapter);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_events, container, false);
        ButterKnife.bind(this, view);
        setupRecyclerView();
        return view;
    }
}
