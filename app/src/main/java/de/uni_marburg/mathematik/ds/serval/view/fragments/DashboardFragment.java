package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.Event;

/**
 * Created by thames1990 on 28.08.17.
 */
public class DashboardFragment<T extends Event> extends Fragment {

    public static final String EVENTS = "EVENTS";

    private List<T> events;

    public static <T extends Event> DashboardFragment<Event> newInstance(ArrayList<T> events) {
        DashboardFragment<Event> fragment = new DashboardFragment<>();
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

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
