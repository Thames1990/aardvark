package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.GenericEventViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;

/**
 * Adapter for {@link GenericEvent generic events}.
 */
public class GenericEventAdapter extends EventAdapter<GenericEvent, GenericEventViewHolder> {
    
    /**
     * Creates a new adapter
     *
     * @param dataSet Data set controlled by the adapter
     */
    public GenericEventAdapter(List<GenericEvent> dataSet) {
        super(dataSet);
    }
    
    @Override
    protected void onBindViewHolder(
            GenericEventViewHolder holder,
            GenericEvent data,
            int position
    ) {
        
    }
    
    @Override
    public GenericEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenericEventViewHolder(parent, R.layout.event_row);
    }
    
}
