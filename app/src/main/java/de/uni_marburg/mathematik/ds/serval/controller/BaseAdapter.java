package de.uni_marburg.mathematik.ds.serval.controller;

import android.os.Handler;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.model.Event;

import static android.support.v7.widget.RecyclerView.*;

/**
 * Generic {@link Adapter Adapter} for {@link Event events}
 * <p>
 * Has the ability to remove events and recover them again in a timeframe of
 * {@link BaseAdapter#PENDING_REMOVAL_TIMEOUT} seconds.
 */

abstract class BaseAdapter<T extends Event, VH extends BaseViewHolder<T>>
        extends RecyclerView.Adapter<VH> {

    /**
     * Time, after which an event pending removal is removed
     */
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;

    /**
     * Items controlled by the adapter
     */
    private List<T> events;

    /**
     * Events pending removal
     */
    private List<T> eventsPendingRemoval;

    /**
     * Saves events pending removal and their temporal state
     */
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private HashMap<T, Runnable> pendingRunnables;

    /**
     * Handles the time after which events are removed
     */
    private Handler handler;

    BaseAdapter(List<T> events) {
        this.events = events;
        this.eventsPendingRemoval = new ArrayList<>();
        this.pendingRunnables = new HashMap<>();
        this.handler = new Handler();
    }

    /**
     * Adds an item to the adapter
     *
     * @param event Event to be added
     */
    public void addEvent(T event) {
        events.add(event);
        notifyItemInserted(getItemCount() - 1);
    }

    /**
     * Removes an event from the adapter
     *
     * @param position Position of the event to be removed
     */
    private void removeEvent(int position) {
        final T event = events.get(position);
        if (eventsPendingRemoval.contains(event)) {
            eventsPendingRemoval.remove(event);
        }
        events.remove(position);
        notifyItemRemoved(position);
    }

    /**
     * Removes a range of events from the adapter
     *
     * @param positionStart Starting position of the range
     * @param eventCount    Number of events to be removed
     */
    private void removeRange(int positionStart, int eventCount) {
        for (int i = 0; i < eventCount; i++) {
            events.remove(positionStart);
        }
        notifyItemRangeRemoved(positionStart, eventCount);
    }

    /**
     * Removes all events from the adapter
     */
    public void removeAll() {
        removeRange(0, events.size());
    }

    /**
     * Instructs the adapter to add an event to the pending removals
     *
     * @param position Position of the event pending removal
     */
    public void pendingRemoval(int position) {
        final T event = events.get(position);
        if (!eventsPendingRemoval.contains(event)) {
            eventsPendingRemoval.add(event);
            notifyItemChanged(position);
            Runnable pendingRemovalRunnable = () -> removeEvent(events.indexOf(event));
            handler.postDelayed(pendingRemovalRunnable, PENDING_REMOVAL_TIMEOUT);
            pendingRunnables.put(event, pendingRemovalRunnable);
        }
    }

    /**
     * Checks whether an event at a given position is already pending removal.
     *
     * @param position Position of the event in the dataset.
     * @return {@code True}, if the event at the given position in the dataset is pending removal;
     * {@code false} otherwise.
     */
    public boolean isPendingRemoval(int position) {
        return eventsPendingRemoval.contains(events.get(position));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    /**
     * Is used as an extension to {@link BaseAdapter#onBindViewHolder(BaseViewHolder, int)} to
     * pass the corresponding event.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 event at the given position in the data set.
     * @param item     The event at the given position in the dataset.
     * @param position The position of the event within the adapter's data set.
     */
    protected abstract void onBindViewHolder(VH holder, T item, int position);

    @Override
    public void onBindViewHolder(VH holder, int position) {
        T event = events.get(position);
        holder.performBind(event, position);
        onBindViewHolder(holder, event, position);
    }
}
