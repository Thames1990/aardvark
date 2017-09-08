package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.location.Location;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.view_holders.BaseViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.comparators.LocationComparator;
import de.uni_marburg.mathematik.ds.serval.model.comparators.TimeComparator;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;

import static android.support.v7.widget.RecyclerView.Adapter;

/**
 * Generic {@link Adapter Adapter} for {@link Event events}
 * <p>
 * Has the ability to removeEvent events and recover them again in a timeframe of
 * {@link BaseAdapter#PENDING_REMOVAL_TIMEOUT} seconds.
 */
public abstract class BaseAdapter<T extends Event, VH extends BaseViewHolder<T>>
        extends RecyclerView.Adapter<VH> {
    
    /**
     * Time, after which an event pending removal is removed
     */
    private static final int PENDING_REMOVAL_TIMEOUT = 3000;
    
    /**
     * Events controlled by the adapter
     */
    private List<T> events;
    
    /**
     * Handles the time after which events are removed
     */
    private Handler handler;
    
    /**
     * Events pending removal
     */
    private List<T> eventsPendingRemoval;
    
    /**
     * Saves events pending removal and their temporal state
     */
    private HashMap<T, Runnable> pendingRunnables;
    
    BaseAdapter(List<T> events) {
        this.events = events;
        this.eventsPendingRemoval = new ArrayList<>();
        this.handler = new Handler();
        this.pendingRunnables = new HashMap<>();
    }
    
    @Override
    public int getItemCount() {
        return events.size();
    }
    
    /**
     * Is used as an extension to {@link BaseAdapter#onBindViewHolder(BaseViewHolder, int)} to
     * pass the corresponding event.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the event
     *                 at the given position in the data set.
     * @param event    The event at the given position in the dataset.
     * @param position The position of the event within the adapter's data set.
     */
    protected abstract void onBindViewHolder(VH holder, T event, int position);
    
    @Override
    public void onBindViewHolder(VH holder, int position) {
        T event = events.get(position);
        holder.performBind(event, position);
        onBindViewHolder(holder, event, position);
    }
    
    /**
     * Adds an event to the adapter
     *
     * @param event Event to be added
     */
    public void addEvent(T event) {
        events.add(event);
        notifyItemInserted(events.size() - 1);
    }
    
    /**
     * Removes an event from the adapter.
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
     * Removes a range of events from the adapter.
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
     * Instructs the adapter to add an event to the pending removals.
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
     * Instructs the adapter to remove an event from the pending removals.
     *
     * @param position Position of the event pending removal
     */
    void undoPendingRemoval(int position) {
        T event = events.get(position);
        Runnable pendingRemovalRunnable = pendingRunnables.get(event);
        pendingRunnables.remove(event);
        if (pendingRemovalRunnable != null) {
            handler.removeCallbacks(pendingRemovalRunnable);
        }
        eventsPendingRemoval.remove(event);
        notifyItemChanged(events.indexOf(event));
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
    
    /**
     * Filters events based on a {@link EventComparator event comparator}.
     *
     * @param comparator Determines the sorting
     * @param reversed   Determines the sorting order. If {@code true}, the events are sorted
     *                   ascending; descending otherwise.
     * @param origin     The location to calculate the distance to. Might be {@code Null}, if events
     *                   are filtered by time.
     */
    public void filter(EventComparator comparator, boolean reversed, @Nullable Location origin) {
        switch (comparator) {
            case DISTANCE:
                if (reversed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Collections.sort(
                                events,
                                new LocationComparator<T>(origin).reversed()
                        );
                    } else {
                        Collections.sort(events, new LocationComparator<>(origin));
                        Collections.reverse(events);
                    }
                } else {
                    Collections.sort(events, new LocationComparator<>(origin));
                }
                break;
            case TIME:
                if (reversed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Collections.sort(
                                events,
                                new TimeComparator<T>().reversed()
                        );
                    } else {
                        Collections.sort(events, new TimeComparator<>());
                        Collections.reverse(events);
                    }
                } else {
                    Collections.sort(events, new TimeComparator<>());
                }
                break;
            default:
                break;
        }
        notifyDataSetChanged();
    }
}
