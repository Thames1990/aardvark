package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.support.annotation.LayoutRes;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.view.ViewGroup;
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

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;
import de.uni_marburg.mathematik.ds.serval.model.event.MeasurementType;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * ViewHolder for {@link GenericEvent generic events}.
 */
public class GenericEventViewHolder extends EventViewHolder<GenericEvent> {
    
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.location_icon)
    ImageView locationIcon;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.location)
    TextView location;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.time)
    TextView time;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.measurement_types)
    LinearLayout measurementTypes;
    
    /**
     * Creates a new ViewHolder.
     *
     * @param parent       Parent ViewGroup
     * @param itemLayoutId Layout resource id
     */
    public GenericEventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
    }
    
    @Override
    protected void onBind(GenericEvent event, int position) {
        setData(event);
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
     * Sets the elapsed time since {@link GenericEventViewHolder#data this event} happened.
     */
    private void setupTime() {
        Calendar calendar = Calendar.getInstance();
        long timeDifference = calendar.getTimeInMillis() - getData().getTime();
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
            time.setText(format.format(getData().getTime()));
        }
    }
    
    /**
     * Sets the distance from the {@link MainActivity#lastLocation last location} to the location of
     * the {@link GenericEventViewHolder#data event}.
     */
    private void setupLocation() {
        Location lastLocation = MainActivity.lastLocation;
        // Location permissions are revoked/denied
        if (lastLocation != null) {
            locationIcon.setVisibility(View.VISIBLE);
            location.setVisibility(View.VISIBLE);
            
            Drawable icon = ContextCompat.getDrawable(context, R.drawable.location);
            icon.setColorFilter(
                    ContextCompat.getColor(context, R.color.icon_mute),
                    PorterDuff.Mode.SRC_IN
            );
            locationIcon.setImageDrawable(icon);
            
            float distance = getData().getLocation().distanceTo(lastLocation);
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
     * Loads icons for each {@link Measurement measurement} available for {@link
     * GenericEventViewHolder#data the event}.
     */
    private void setupMeasurementIcons() {
        measurementTypes.removeAllViews();
        Set<MeasurementType> types = new HashSet<>();
        
        for (Measurement measurement : getData().getMeasurements()) {
            types.add(measurement.getType());
        }
        
        for (MeasurementType type : types) {
            ImageView icon = new ImageView(context);
            icon.setImageResource(type.getResId(context));
            icon.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            measurementTypes.addView(icon);
        }
    }
}
