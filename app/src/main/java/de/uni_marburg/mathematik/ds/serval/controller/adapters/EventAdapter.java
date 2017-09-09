package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;

import java.util.Collections;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.view_holders.EventViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.comparators.LocationComparator;
import de.uni_marburg.mathematik.ds.serval.model.comparators.TimeComparator;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;

import static android.support.v7.widget.RecyclerView.Adapter;

/**
 * Generic {@link Adapter Adapter} for {@link Event events}.
 * <p>
 * Has the ability to remove events and recover them again in a timeframe of {@link
 * EventAdapter#PENDING_REMOVAL_TIMEOUT} seconds.
 */
abstract class EventAdapter<T extends Event, VH extends EventViewHolder<T>>
        extends BaseAdapter<T, VH> {
    
    
    EventAdapter(List<T> dataSet) {
        super(dataSet);
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
                                dataSet,
                                new LocationComparator<T>(origin).reversed()
                        );
                    } else {
                        Collections.sort(dataSet, new LocationComparator<>(origin));
                        Collections.reverse(dataSet);
                    }
                } else {
                    Collections.sort(dataSet, new LocationComparator<>(origin));
                }
                break;
            case TIME:
                if (reversed) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Collections.sort(
                                dataSet,
                                new TimeComparator<T>().reversed()
                        );
                    } else {
                        Collections.sort(dataSet, new TimeComparator<>());
                        Collections.reverse(dataSet);
                    }
                } else {
                    Collections.sort(dataSet, new TimeComparator<>());
                }
                break;
            default:
                break;
        }
        notifyDataSetChanged();
    }
}
