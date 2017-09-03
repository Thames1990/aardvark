package de.uni_marburg.mathematik.ds.serval.controller;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.model.Event;

/**
 * Generic {@link ViewHolder ViewHolder} for {@link Event events}
 * <p>
 * Has Listener for normal and long clicks.
 */
public abstract class BaseViewHolder<T extends Event>
        extends ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    
    private T event;
    
    BaseViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false));
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    
    /**
     * Called when an event should be bound.
     *
     * @param event    The event that should be bound.
     * @param position The position of the event that should be bound.
     */
    public final void performBind(T event, int position) {
        this.event = event;
        onBind(event, position);
    }
    
    public T getEvent() {
        return event;
    }
    
    /**
     * Called when an event is bound. Is used to set the attributes of an event.
     *
     * @param event    The event that is bound.
     * @param position The position of the event that is bound.
     */
    protected abstract void onBind(T event, int position);
    
    /**
     * @param view  The view that was clicked.
     * @param event The event that was clicked.
     */
    protected abstract void onClick(View view, T event);
    
    /**
     * Is used as an extension to {@link BaseViewHolder#onLongClick(View)} to pass the
     * corresponding event.
     *
     * @param view  The view that was clicked and held.
     * @param event The event that was clicked and held.
     * @return {@code True} if the callback consumed the long click; {@code false} otherwise.
     */
    protected abstract boolean onLongClick(View view, T event);
    
    @Override
    public final void onClick(View view) {
        onClick(view, event);
    }
    
    @Override
    public final boolean onLongClick(View view) {
        return onLongClick(view, event);
    }
}
