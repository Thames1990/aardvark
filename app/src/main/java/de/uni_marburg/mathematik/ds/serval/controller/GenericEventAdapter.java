package de.uni_marburg.mathematik.ds.serval.controller;

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
        return new GenericEventViewHolder(parent, R.layout.item_card);
    }

    @Override
    protected void onBindViewHolder(
            GenericEventViewHolder holder,
            GenericEvent event,
            int position
    ) {

    }

}
