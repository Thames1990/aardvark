package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by thames1990 on 09.09.17.
 */
public class MeasurementsViewHolder
        extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    
    public MeasurementsViewHolder(View itemView) {
        super(itemView);
    }
    
    @Override
    public void onClick(View view) {
        
    }
    
    @Override
    public boolean onLongClick(View view) {
        return false;
    }
}
