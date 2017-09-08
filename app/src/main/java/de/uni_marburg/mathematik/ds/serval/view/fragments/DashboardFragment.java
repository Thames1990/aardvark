package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;

/**
 * Created by thames1990 on 28.08.17.
 */
public class DashboardFragment extends BaseFragment {
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestEvents(EventComparator.DISTANCE, false, EVENT_COUNT);
    }
    
    @Override
    protected int getLayout() {
        return R.layout.fragment_dashboard;
    }
}
