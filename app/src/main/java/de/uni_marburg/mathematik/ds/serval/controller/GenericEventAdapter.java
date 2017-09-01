package de.uni_marburg.mathematik.ds.serval.controller;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;

/**
 * Adapter for {@link GenericEvent generic events}
 */
public class GenericEventAdapter extends BaseAdapter<GenericEvent, GenericEventViewHolder> {
    
    private GenericEventViewHolder holder;
    
    public GenericEventAdapter(List<GenericEvent> events) {
        super(events);
    }
    
    @Override
    public GenericEventViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new GenericEventViewHolder(parent, R.layout.event_row);
    }
    
    @Override
    protected void onBindViewHolder(
            GenericEventViewHolder holder,
            GenericEvent event,
            int position
    ) {
        this.holder = holder;
    }
    
    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        super.onDetachedFromRecyclerView(recyclerView);
        // TODO Lets see if this works
        holder.locationManager.removeUpdates(holder);
    }
}
