package de.uni_marburg.mathematik.ds.serval.controller;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;

/**
 * Adapter for {@link GenericEvent generic events}
 */
public class GenericEventAdapter extends BaseAdapter<GenericEvent, GenericEventViewHolder> {
    
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
        if (isPendingRemoval(position)) {
            holder.itemView.setBackgroundColor(Color.RED);
            holder.measurementTypes.setVisibility(View.GONE);
            holder.time.setVisibility(View.GONE);
            holder.location.setVisibility(View.GONE);
            holder.undo.setVisibility(View.VISIBLE);
            holder.undo.setOnClickListener(view -> undoPendingRemoval(position));
        } else {
            holder.itemView.setBackgroundColor(Color.WHITE);
            holder.measurementTypes.setVisibility(View.VISIBLE);
            holder.time.setVisibility(View.VISIBLE);
            holder.location.setVisibility(View.VISIBLE);
            holder.undo.setVisibility(View.GONE);
            holder.undo.setOnClickListener(null);
        }
    }
}
