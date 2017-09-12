package de.uni_marburg.mathematik.ds.serval.model.event;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.tasks.EventAsyncTask;

/**
 * Links different {@link android.content.Context contexts}. Informs about the completion of {@link
 * Event event} loading in {@link EventAsyncTask an asynchronous task} or a request for this task.
 *
 * @param <T> Type of the {@link Event event}
 */
public interface EventCallback<T extends Event> {
    
    /**
     * Indicates that {@link Event events} are loaded from {@link EventAsyncTask an asynchronous
     * event task}.
     *
     * @param events Events that were loaded
     */
    void onEventsLoaded(List<T> events);
    
    /**
     * Informs a {@link android.content.Context context} that {@link Event events} are requested.
     *
     * @param comparator Determines how the {@link Event events} should be sorted
     * @param reversed   Determines whether the {@link EventComparator comparator} should be
     *                   reversed.
     * @param count      Number of requested {@link Event events}
     * @return A (sorted) list of {@link Event events}
     */
    List<T> onEventsRequested(EventComparator comparator, boolean reversed, int count);
    
}
