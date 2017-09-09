package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.view.ViewGroup;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.view_holders.MeasurementsViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;

/**
 * Created by thames1990 on 09.09.17.
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
    
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
}
