package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.support.v7.widget.RecyclerView;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.view_holders.BaseViewHolder;

/**
 * Generic {@link android.support.v7.widget.RecyclerView.Adapter adapter}.
 *
 * @param <T>  {@link BaseAdapter#dataSet Data set} type
 * @param <VH> {@link BaseViewHolder ViewHolder} type
 */
abstract class BaseAdapter<T, VH extends BaseViewHolder<T>> extends RecyclerView.Adapter<VH> {
    
    /**
     * Data set controlled by the adapter
     */
    List<T> dataSet;
    
    /**
     * Creates a new adapter
     *
     * @param dataSet Data set controlled by the adapter
     */
    BaseAdapter(List<T> dataSet) {
        this.dataSet = dataSet;
    }
    
    @Override
    public void onBindViewHolder(VH holder, int position) {
        T data = dataSet.get(position);
        holder.performBind(data, position);
        onBindViewHolder(holder, data, position);
    }
    
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    
    /**
     * Is used as an extension to {@link BaseAdapter#onBindViewHolder(VH, int)} to pass the
     * corresponding data.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the data
     *                 at the given position in the data set.
     * @param data     The data at the given position in the data set.
     * @param position The position of the data within the adapter's data set.
     */
    protected abstract void onBindViewHolder(VH holder, T data, int position);
    
    /**
     * Changes the data set of the adapter and updates the UI.
     *
     * @param dataSet New data set
     */
    public void setDataSet(List<T> dataSet) {
        this.dataSet = dataSet;
        notifyDataSetChanged();
    }
}
