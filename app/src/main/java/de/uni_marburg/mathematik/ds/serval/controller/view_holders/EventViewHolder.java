package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Generic {@link BaseViewHolder ViewHolder} ofr {@link Event events}.
 *
 * @param <T> {@link Event} type
 */
public abstract class EventViewHolder<T extends Event>
        extends BaseViewHolder<T>
        implements View.OnClickListener, View.OnLongClickListener {
    
    /**
     * Creates a new ViewHolder.
     *
     * @param parent       Parent ViewGroup
     * @param itemLayoutId Layout resource id
     */
    EventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
    }
}
