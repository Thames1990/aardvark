package de.uni_marburg.mathematik.ds.serval.view.fragments;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Created by thames1990 on 28.08.17.
 */
public class DashboardFragment<T extends Event> extends EventFragment<T> {
    
    @Override
    protected int getLayout() {
        return R.layout.fragment_dashboard;
    }
}
