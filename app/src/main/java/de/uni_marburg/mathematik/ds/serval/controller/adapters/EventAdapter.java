package de.uni_marburg.mathematik.ds.serval.controller.adapters;

import android.location.Location;

import java.util.Collections;
import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.view_holders.EventViewHolder;
import de.uni_marburg.mathematik.ds.serval.model.comparators.LocationComparator;
import de.uni_marburg.mathematik.ds.serval.model.comparators.MeasurementsComparator;
import de.uni_marburg.mathematik.ds.serval.model.comparators.TimeComparator;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;

/**
 * Generic adapter for {@link Event events}.
 *
 * @param <T>  {@link Event} type
 * @param <VH> {@link EventViewHolder ViewHolder} type
 */
public abstract class EventAdapter<T extends Event, VH extends EventViewHolder<T>>
        extends BaseAdapter<T, VH> {
    
    /**
     * Creates a new adapter
     *
     * @param dataSet Data set controlled by the adapter
     */
    EventAdapter(List<T> dataSet) {
        super(dataSet);
    }
    
    /**
     * Filters events based on a {@link EventComparator comparator}.
     *
     * @param comparator Determines the sorting
     * @param reversed   Determines the sorting order. If {@code true}, the events are sorted
     *                   descending; ascending otherwise.
     */
    public void filter(EventComparator comparator, boolean reversed) {
        switch (comparator) {
            case MEASUREMENTS:
                MeasurementsComparator<T> measurementsComparator = new MeasurementsComparator<>();
                if (reversed) {
                    Collections.sort(dataSet, measurementsComparator);
                    Collections.reverse(dataSet);
                } else {
                    Collections.sort(dataSet, measurementsComparator);
                }
                break;
            case SHUFFLE:
                Collections.shuffle(dataSet);
                break;
            case TIME:
                TimeComparator<T> timeComparator = new TimeComparator<>();
                if (reversed) {
                    Collections.sort(dataSet, timeComparator);
                    Collections.reverse(dataSet);
                } else {
                    Collections.sort(dataSet, timeComparator);
                }
                break;
            default:
                return;
        }
        notifyDataSetChanged();
    }
    
    /**
     * Filters events based on a {@link EventComparator comparator}.
     *
     * @param comparator Determines the sorting
     * @param reversed   Determines the sorting order. If {@code true}, the events are sorted
     *                   descending; ascending otherwise.
     * @param origin     The location to calculate the distance to
     */
    public void filter(EventComparator comparator, boolean reversed, Location origin) {
        switch (comparator) {
            case DISTANCE:
                LocationComparator<T> locationComparator = new LocationComparator<>(origin);
                if (reversed) {
                    Collections.sort(dataSet, locationComparator);
                    Collections.reverse(dataSet);
                } else {
                    Collections.sort(dataSet, locationComparator);
                }
                break;
            default:
                return;
        }
        notifyDataSetChanged();
    }
}
