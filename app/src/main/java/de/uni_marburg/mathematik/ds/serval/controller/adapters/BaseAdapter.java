package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.view_holders.BaseViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Generic {@link Adapter Adapter} for {@link Event items}
 * <p>
 * Has the ability to removeItem items and recover them again in a timeframe of
 * {@link BaseAdapter#PENDING_REMOVAL_TIMEOUT} seconds.
 */

public abstract class BaseAdapter<T extends Event, VH extends BaseViewHolder<T>>
        extends RecyclerView.Adapter<VH> {
    
    /**
     * Time, after which an event pending removal is removed
     */
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    
    /**
     * Items controlled by the adapter
     */
    private List<T> items;
    
    /**
     * Handles the time after which items are removed
     */
    private Handler handler;
    
    /**
     * Items pending removal
     */
    private List<T> itemsPendingRemoval;
    
    /**
     * Saves items pending removal and their temporal state
     */
    private HashMap<T, Runnable> pendingRunnables;
    
    BaseAdapter(List<T> items) {
        this.items = items;
        this.itemsPendingRemoval = new ArrayList<>();
        this.handler = new Handler();
        this.pendingRunnables = new HashMap<>();
    }
    
    /**
     * Adds an item to the adapter
     *
     * @param item Item to be added
     */
    public void addItem(T item) {
        items.add(item);
        notifyItemInserted(items.size() - 1);
    }
    
    /**
     * Removes an item from the adapter.
     *
     * @param position Position of the item to be removed
     */
    private void removeItem(int position) {
        final T item = items.get(position);
        if (itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.remove(item);
        }
        items.remove(position);
        notifyItemRemoved(position);
    }
    
    /**
     * Removes a range of items from the adapter.
     *
     * @param positionStart Starting position of the range
     * @param eventCount    Number of items to be removed
     */
    private void removeRange(int positionStart, int eventCount) {
        for (int i = 0; i < eventCount; i++) {
            items.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, eventCount);
    }
    
    /**
     * Instructs the adapter to add an item to the pending removals.
     *
     * @param position Position of the item pending removal
     */
    public void pendingRemoval(int position) {
        final T item = items.get(position);
        if (!itemsPendingRemoval.contains(item)) {
            itemsPendingRemoval.add(item);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = () -> removeItem(items.indexOf(item));
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(item, pendingRemovalRunnable);
        }
    }
    
    /**
     * Instructs the adapter to remove an item from the pending removals.
     *
     * @param position Position of the item pending removal
     */
    void undoPendingRemoval(int position) {
        T item = items.get(position);
        Runnable pendingRemovalRunnable = pendingRunnables.get(item);
        pendingRunnables.remove(item);
        if (pendingRemovalRunnable != null) {
            handler.removeCallbacks(pendingRemovalRunnable);
        }
        itemsPendingRemoval.remove(item);
        notifyItemChanged(items.indexOf(item));
    }
    
    /**
     * Checks whether an item at a given position is already pending removal.
     *
     * @param position Position of the item in the dataset.
     * @return {@code True}, if the item at the given position in the dataset is pending removal;
     * {@code false} otherwise.
     */
    public boolean isPendingRemoval(int position) {
        return itemsPendingRemoval.contains(items.get(position));
    }
    
    @Override
    public int getItemCount() {
        return items.size();
    }
    
    /**
     * Is used as an extension to {@link BaseAdapter#onBindViewHolder(BaseViewHolder, int)} to
     * pass the corresponding item.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the item
     *                 at the given position in the data set.
     * @param item     The item at the given position in the dataset.
     * @param position The position of the item within the adapter's data set.
     */
    protected abstract void onBindViewHolder(VH holder, T item, int position);
    
    @Override
    public void onBindViewHolder(VH holder, int position) {
        T item = items.get(position);
        holder.performBind(item, position);
        onBindViewHolder(holder, item, position);
    }
}
