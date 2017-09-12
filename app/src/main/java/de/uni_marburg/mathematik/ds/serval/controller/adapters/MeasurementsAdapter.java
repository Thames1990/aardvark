package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.MeasurementsViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;

/**
 * Adapter for {@link Measurement measurements}.
 */
public class MeasurementsAdapter extends BaseAdapter<Measurement, MeasurementsViewHolder> {
    
    public MeasurementsAdapter(List<Measurement> dataSet) {
        super(dataSet);
    }
    
    @Override
    public MeasurementsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MeasurementsViewHolder(parent, R.layout.measurement_row);
    }
    
    @Override
    protected void onBindViewHolder(MeasurementsViewHolder holder, Measurement data, int position) {
        
    }
}
