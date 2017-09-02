package de.uni_marburg.mathematik.ds.serval.view.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by thames1990 on 02.09.17.
 */
public class SwipeToDeleteItemDecoration extends RecyclerView.ItemDecoration {
    
    private Drawable background;
    
    public SwipeToDeleteItemDecoration() {
        background = new ColorDrawable(Color.RED);
    }
    
    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        // If animation is in progress
        if (parent.getItemAnimator().isRunning()) {
            // Some items might be animating down and some items might be animating up to close
            // the gap left by the removed item, This is not exclusive, both movements can be
            // happening at the same time to reproduce this leave just enough items so the first
            // one and the last one would be just a little off screen then remove one from the
            // middle.
            View lastViewComingDown = null;
            View firstViewComingUp = null;
            
            int left = 0;
            int right = parent.getWidth();
            int top = 0;
            int bottom = 0;
            
            // Find relevant translating views
            int childCount = parent.getLayoutManager().getChildCount();
            for (int i = 0; i < childCount; i++) {
                View child = parent.getLayoutManager().getChildAt(i);
                if (child.getTranslationY() < 0) {
                    lastViewComingDown = child;
                } else if (child.getTranslationY() > 0) {
                    if (firstViewComingUp == null) {
                        firstViewComingUp = child;
                    }
                }
            }
            
            if (lastViewComingDown != null && firstViewComingUp != null) {
                // Views are going down and up to fill the void
                top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
            } else if (lastViewComingDown != null) {
                // Views are going down to fill the void
                top = lastViewComingDown.getBottom() + (int) lastViewComingDown.getTranslationY();
                bottom = lastViewComingDown.getBottom();
            } else if (firstViewComingUp != null) {
                // Views are going up to fill the void
                top = firstViewComingUp.getTop();
                bottom = firstViewComingUp.getTop() + (int) firstViewComingUp.getTranslationY();
            }
            
            background.setBounds(left, top, right, bottom);
            background.draw(c);
        }
        
        super.onDraw(c, parent, state);
    }
}
