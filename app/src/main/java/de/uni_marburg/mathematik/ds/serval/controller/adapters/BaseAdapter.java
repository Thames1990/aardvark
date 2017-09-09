package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.view_holders.BaseViewHolder;

/**
 * Generic {@link RecyclerView.Adapter Adapter}.
 * <p>
 * Has the ability to remove data and recover it again in a timeframe of {@link
 * BaseAdapter#PENDING_REMOVAL_TIMEOUT} seconds.
 */
public abstract class BaseAdapter<T, VH extends BaseViewHolder> extends RecyclerView.Adapter<VH> {
    
    /**
     * Time, after which data pending removal is removed
     */
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    
    /**
     * Data set controlled by the adapter
     */
    List<T> dataSet;
    
    /**
     * Handles the time after which data is removed
     */
    private Handler handler;
    
    /**
     * Data pending removal
     */
    private List<T> dataPendingRemoval;
    
    /**
     * Saves data pending removal and their temporal state
     */
    private HashMap<T, Runnable> pendingRunnables;
    
    BaseAdapter(List<T> dataSet) {
        this.dataSet = dataSet;
        this.dataPendingRemoval = new ArrayList<>();
        this.handler = new Handler();
        this.pendingRunnables = new HashMap<>();
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
    
    @Override
    public void onBindViewHolder(VH holder, int position) {
        T data = dataSet.get(position);
        //noinspection unchecked
        holder.performBind(data, position);
        onBindViewHolder(holder, data, position);
    }
    
    @Override
    public int getItemCount() {
        return dataSet.size();
    }
    
    /**
     * Adds data to the adapter.
     *
     * @param data Data to be added
     */
    public void addData(T data) {
        dataSet.add(data);
        notifyItemInserted(dataSet.size() - 1);
    }
    
    /**
     * Removes data from the adapter.
     *
     * @param position Position of the data to be removed
     */
    private void removeData(int position) {
        final T data = dataSet.get(position);
        if (dataPendingRemoval.contains(data)) {
            dataPendingRemoval.remove(data);
        }
        dataSet.remove(position);
        notifyItemRemoved(position);
    }
    
    /**
     * Removes a range of data objects from the adapter.
     *
     * @param positionStart Starting position of the range
     * @param dataCount     Number of data objects to be removed
     */
    private void removeRange(int positionStart, int dataCount) {
        for (int i = 0; i < dataCount; i++) {
            dataSet.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, dataCount);
    }
    
    /**
     * Instructs the adapter to add data to the pending removals.
     *
     * @param position Position of the data pending removal
     */
    public void pendingRemoval(int position) {
        final T data = dataSet.get(position);
        if (!dataPendingRemoval.contains(data)) {
            dataPendingRemoval.add(data);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = () -> removeData(dataSet.indexOf(data));
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(data, pendingRemovalRunnable);
        }
    }
    
    /**
     * Instructs the adapter to remove data from the pending removals.
     *
     * @param position Position of the data pending removal
     */
    void undoPendingRemoval(int position) {
        T data = dataSet.get(position);
        Runnable pendingRemovalRunnable = pendingRunnables.get(data);
        pendingRunnables.remove(data);
        if (pendingRemovalRunnable != null) {
            handler.removeCallbacks(pendingRemovalRunnable);
        }
        dataPendingRemoval.remove(data);
        notifyItemChanged(dataSet.indexOf(data));
    }
    
    /**
     * Checks whether data at a given position is already pending removal.
     *
     * @param position Position of the data in the data set.
     * @return {@code True}, if the data at the given position in the data set is pending removal;
     * {@code false} otherwise.
     */
    public boolean isPendingRemoval(int position) {
        return dataPendingRemoval.contains(dataSet.get(position));
    }
}
