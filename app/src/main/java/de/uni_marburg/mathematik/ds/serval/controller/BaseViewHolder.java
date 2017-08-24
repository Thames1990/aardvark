package de.uni_marburg.mathematik.ds.serval.controller;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.model.Item;

/**
 * Generic {@link android.support.v7.widget.RecyclerView.ViewHolder ViewHolder} for
 * {@link Item generic data}
 */
abstract class BaseViewHolder<T extends Item>
        extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {

    private T item;

    BaseViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false));
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }

    /**
     * Called when an item should be bound.
     *
     * @param item     The item that should be bound.
     * @param position The position of the item that should be bound.
     */
    final void performBind(T item, int position) {
        this.item = item;
        onBind(item, position);
    }

    public T getItem() {
        return item;
    }

    /**
     * Called when an item is bound. Is used to set the attributes of an item.
     *
     * @param item     The item that is bound.
     * @param position The position of the item that is bound.
     */
    protected abstract void onBind(T item, int position);

    /**
     * @param view The view that was clicked.
     * @param item The item that was clicked.
     */
    protected abstract void onClick(View view, T item);

    /**
     * Is used as an extension to {@link BaseViewHolder#onLongClick(View)} to pass the
     * corresponding item.
     *
     * @param view The view that was clicked and held.
     * @param item The item that was clicked and held.
     * @return {@code True} if the callback consumed the long click; {@code false} otherwise.
     */
    protected abstract boolean onLongClick(View view, T item);

    @Override
    public final void onClick(View view) {
        onClick(view, item);
    }

    @Override
    public final boolean onLongClick(View view) {
        return onLongClick(view, item);
    }
}
