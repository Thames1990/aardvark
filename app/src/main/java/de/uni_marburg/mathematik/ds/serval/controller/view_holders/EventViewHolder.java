package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.View;
import android.view.ViewGroup;

import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Generic {@link ViewHolder ViewHolder} for {@link Event events}
 * <p>
 * Has listeners for normal and long clicks.
 */
public abstract class EventViewHolder<T extends Event>
        extends BaseViewHolder<T>
        implements View.OnClickListener, View.OnLongClickListener {
    
    EventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
    }
}
