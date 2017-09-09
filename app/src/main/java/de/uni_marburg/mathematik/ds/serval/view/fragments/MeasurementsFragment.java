package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;
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
}
