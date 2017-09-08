package de.uni_marburg.mathematik.ds.serval.view.item_touch_helpers;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.BaseAdapter;

/**
 * Created by thames1990 on 02.09.17.
 */
public class SwipeToDeleteItemTouchHelper<A extends BaseAdapter>
        extends ItemTouchHelper.SimpleCallback {
    
    private Drawable background;
    
    private Drawable clear;
    
    private int clearMargin;
    
    private A adapter;
    
    public SwipeToDeleteItemTouchHelper(Context context) {
        super(0, ItemTouchHelper.LEFT);
        background = new ColorDrawable(Color.RED);
        clear = ContextCompat.getDrawable(context, R.drawable.clear);
        clear.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        clearMargin = (int) context.getResources().getDimension(R.dimen.margin_clear);
    }
    
    @Override
    public boolean onMove(
            RecyclerView recyclerView,
            RecyclerView.ViewHolder viewHolder,
            RecyclerView.ViewHolder target
    ) {
        return false;
    }
    
    @Override
    public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int position = viewHolder.getAdapterPosition();
        //noinspection unchecked
        adapter = (A) recyclerView.getAdapter();
        if (adapter.isPendingRemoval(position)) {
            return 0;
        }
        return super.getSwipeDirs(recyclerView, viewHolder);
    }
    
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        int swipedPosition = viewHolder.getAdapterPosition();
        adapter.pendingRemoval(swipedPosition);
    }
    
    @Override
    public void onChildDraw(
            Canvas c,
            RecyclerView recyclerView,
            RecyclerView.ViewHolder viewHolder,
            float dX,
            float dY,
            int actionState,
            boolean isCurrentlyActive
    ) {
        View itemView = viewHolder.itemView;
        
        if (viewHolder.getAdapterPosition() == -1) {
            return;
        }
        
        drawBackground(c, (int) dX, itemView);
        drawClear(c, itemView);
        
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
    
    /**
     * Draw red background
     *
     * @param c        The canvas which RecyclerView is drawing its children
     * @param dX       The amount of horizontal displacement caused by user's action
     * @param itemView Corresponding view
     */
    private void drawBackground(Canvas c, int dX, View itemView) {
        background.setBounds(
                itemView.getRight() + dX,
                itemView.getTop(),
                itemView.getRight(),
                itemView.getBottom()
        );
        background.draw(c);
    }
    
    /**
     * Draw clear icon
     *
     * @param c        The canvas which RecyclerView is drawing its children
     * @param itemView Corresponding view
     */
    private void drawClear(Canvas c, View itemView) {
        int itemHeight = itemView.getBottom() - itemView.getTop();
        int intrinsicWidth = clear.getIntrinsicWidth();
        int intrinsicHeight = clear.getIntrinsicHeight();
        
        int clearLeft = itemView.getRight() - clearMargin - intrinsicWidth;
        int clearRight = itemView.getRight() - clearMargin;
        int clearTop = itemView.getTop() + (itemHeight - intrinsicHeight) / 2;
        int clearBottom = clearTop + intrinsicHeight;
        
        clear.setBounds(clearLeft, clearTop, clearRight, clearBottom);
        
        clear.draw(c);
    }
}
