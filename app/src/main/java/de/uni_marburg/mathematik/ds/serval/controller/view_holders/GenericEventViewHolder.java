package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.content.Intent;
import android.location.Location;
import android.support.annotation.LayoutRes;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;
import de.uni_marburg.mathematik.ds.serval.model.event.MeasurementType;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * ViewHolder for {@link GenericEvent generic events}
 */
public class GenericEventViewHolder extends EventViewHolder<GenericEvent> {
    
    @BindView(R.id.undo)
    public Button undo;
    @BindView(R.id.measurement_types)
    public LinearLayout measurementTypes;
    @BindView(R.id.time)
    public TextView time;
    @BindView(R.id.location_icon)
    ImageView locationIcon;
    @BindView(R.id.location)
    public TextView location;
    
    public GenericEventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
    }
    
    @Override
    protected void onBind(GenericEvent event, int position) {
        data = event;
        setupTime();
        setupLocation();
        setupMeasurementIcons();
    }
    
    @Override
    protected void onClick(View view, GenericEvent event) {
        Intent detail = new Intent(context, DetailActivity.class);
        detail.putExtra(DetailActivity.EVENT, event);
        context.startActivity(detail);
    }
    
    @Override
    protected boolean onLongClick(View view, GenericEvent event) {
        return false;
    }
    
    /**
     * Sets the elapsed days since an {@link Event event} happened.
     */
    private void setupTime() {
        Calendar calendar = Calendar.getInstance();
        long timeDifference = calendar.getTimeInMillis() - data.getTime();
        Log.d("TEST", "setupTime: " + timeDifference);
        DateFormat format = SimpleDateFormat.getDateInstance(
                DateFormat.MEDIUM,
                Locale.getDefault()
        );
        if (TimeUnit.MILLISECONDS.toMinutes(timeDifference) < 60) {
            time.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_minutes_ago),
                    TimeUnit.MILLISECONDS.toMinutes(timeDifference)
            ));
        } else if (TimeUnit.MILLISECONDS.toHours(timeDifference) < 24) {
            time.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_hours_ago),
                    TimeUnit.MILLISECONDS.toHours(timeDifference)
            ));
        } else if (TimeUnit.MILLISECONDS.toDays(timeDifference) < 7) {
            time.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_days_ago),
                    TimeUnit.MILLISECONDS.toDays(timeDifference)
            ));
        } else {
            time.setText(format.format(data.getTime()));
        }
    }
    
    /**
     * Sets the distance from the current position to an {@link Event events} position.
     */
    private void setupLocation() {
        Location lastLocation = ((MainActivity) context).getLastLocation();
        if (lastLocation != null) {
            locationIcon.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            
            float distance = data.getLocation().distanceTo(lastLocation);
            if (distance < 1000) {
                location.setText(String.format(
                        Locale.getDefault(),
                        context.getString(R.string.location_distance_to_meter),
                        distance
                ));
            } else {
                location.setText(String.format(
                        Locale.getDefault(),
                        context.getString(R.string.location_distance_to_kilometer),
                        distance / 1000
                ));
            }
        } else {
            locationIcon.setVisibility(View.GONE);
            location.setVisibility(View.GONE);
        }
    }
    
    /**
     * Loads icons for each measurement type available in the measurements of the event.
     */
    private void setupMeasurementIcons() {
        measurementTypes.removeAllViews();
        Set<MeasurementType> types = new HashSet<>();
        
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            types = data.getMeasurements()
                        .stream()
                        .map(Measurement::getType)
                        .collect(Collectors.toSet());
        } else {
            for (Measurement measurement : data.getMeasurements()) {
                types.add(measurement.getType());
            }
        }
        
        for (MeasurementType type : types) {
            ImageView icon = new ImageView(context);
            icon.setImageResource(type.getResId(context));
            icon.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            measurementTypes.addView(icon);
        }
    }
}
