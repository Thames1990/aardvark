package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

/**
 * Created by thames1990 on 09.09.17.
 */
public abstract class BaseViewHolder<T>
        extends RecyclerView.ViewHolder
        implements View.OnClickListener, View.OnLongClickListener {
    
    Context context;
    
    /**
     * Data that is displayed in the view
     */
    T data;
    
    BaseViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, parent, false));
        context = parent.getContext();
        ButterKnife.bind(this, itemView);
        itemView.setOnClickListener(this);
        itemView.setOnLongClickListener(this);
    }
    
    /**
     * Called when data should be bound.
     *
     * @param data     The data that should be bound.
     * @param position The position of the data that should be bound.
     */
    public final void performBind(T data, int position) {
        this.data = data;
        onBind(data, position);
    }
    
    public T getData() {
        return data;
    }
    
    /**
     * Called when data is bound. Is used to set the attributes of data.
     *
     * @param data     The data that is bound.
     * @param position The position of the data that is bound.
     */
    protected abstract void onBind(T data, int position);
    
    /**
     * @param view The view that was clicked.
     * @param data The data that was clicked.
     */
    protected abstract void onClick(View view, T data);
    
    /**
     * Is used as an extension to {@link BaseViewHolder#onLongClick(View)} to pass the
     * corresponding data.
     *
     * @param view The view that was clicked and held.
     * @param data The data that was clicked and held.
     * @return {@code True} if the callback consumed the long click; {@code false} otherwise.
     */
    protected abstract boolean onLongClick(View view, T data);
    
    @Override
    public final void onClick(View view) {
        onClick(view, data);
    }
    
    @Override
    public final boolean onLongClick(View view) {
        return onLongClick(view, data);
    }
}
