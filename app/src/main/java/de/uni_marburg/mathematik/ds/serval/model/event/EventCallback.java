package de.uni_marburg.mathematik.ds.serval.model.event;

import android.app.Activity;
import android.support.v4.app.Fragment;

import java.util.List;

import de.uni_marburg.mathematik.ds.serval.controller.tasks.EventAsyncTask;

/**
 * Is used to link an {@link Activity activity} and {@link Fragment fragments} to indicate that
 * {@link Event events} are requested or {@link EventAsyncTask an asynchronous event task} to start
 * a UI update after {@link Event events} are loaded.
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
     * Informs an {@link Activity activity} that {@link Event events} are requested from a {@link
     * Fragment fragment}.
     *
     * @param comparator Determines how the {@link Event events} should be sorted
     * @param reversed   Determines whether the {@link EventComparator comparator} should be
     *                   reversed.
     * @param count      Number of requested {@link Event events}
     * @return A sorted list of {@link Event events}
     */
    List<T> onEventsRequested(EventComparator comparator, boolean reversed, int count);
    
}
