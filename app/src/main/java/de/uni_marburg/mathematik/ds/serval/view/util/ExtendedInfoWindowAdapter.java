package de.uni_marburg.mathematik.ds.serval.view.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by thames1990 on 29.08.17.
 */
public class ExtendedInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {
    
    private Context context;
    
    public ExtendedInfoWindowAdapter(Context context) {
        this.context = context;
    }
    
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }
    
    @Override
    public View getInfoContents(Marker marker) {
        LinearLayout info = new LinearLayout(context);
        info.setOrientation(LinearLayout.VERTICAL);
        
        TextView title = new TextView(context);
        title.setTextColor(Color.BLACK);
        title.setGravity(Gravity.CENTER);
        title.setTypeface(null, Typeface.BOLD);
        title.setText(marker.getTitle());
        
        TextView snippet = new TextView(context);
        snippet.setTextColor(Color.GRAY);
        snippet.setGravity(Gravity.CENTER);
        snippet.setText(marker.getSnippet());
        
        info.addView(title);
        info.addView(snippet);
        
        return info;
    }
}
