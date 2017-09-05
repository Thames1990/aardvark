package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;

import java.util.Locale;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

/**
 * Created by thames1990 on 24.08.17.
 */

public class MeasurementsFragment<T extends Event> extends Fragment {
    
    /**
     * This key is used to collect the {@link InformationFragment#event event} from the
     * {@link DetailActivity detail activity}.
     */
    public static final String EVENT = "EVENT";
    
    /**
     * Event to show measurements for
     */
    private T event;
    
    private Unbinder unbinder;
    
    public static <T extends Event> MeasurementsFragment<Event> newInstance(T event) {
        MeasurementsFragment<Event> fragment = new MeasurementsFragment<>();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!getArguments().containsKey(EVENT)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    EVENT
            ));
        }
        event = getArguments().getParcelable(EVENT);
    }
    
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_measurements, container, false);
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
        RefWatcher refWatcher = Serval.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
