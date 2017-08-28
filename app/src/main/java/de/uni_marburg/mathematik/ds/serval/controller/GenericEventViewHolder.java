package de.uni_marburg.mathematik.ds.serval.controller;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.model.MeasurementType;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

/**
 * ViewHolder for {@link GenericEvent generic events}
 */
class GenericEventViewHolder extends BaseViewHolder<GenericEvent> {

    private Context context;

    @BindView(R.id.thumbnail)
    ImageView thumbnail;

    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.location)
    TextView location;

    GenericEventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
        context = parent.getContext();
        thumbnail.setOnClickListener(this);
    }

    @Override
    protected void onBind(GenericEvent event, int position) {
        setupThumbnail(event);
        setupTime(event);
        setupLocation(event);
    }

    /**
     * Loads a representation for the measurement type with the most measurements for the event.
     *
     * @param event The corresponding event
     */
    private void setupThumbnail(GenericEvent event) {
        // Count the occurence of each measurement type
        @SuppressLint("UseSparseArrays")
        Map<Long, MeasurementType> count = new HashMap<>();
        for (MeasurementType type : MeasurementType.values()) {
            count.put(
                    event.getMeasurements()
                            .stream()
                            .filter(measurement -> measurement.getType().equals(type))
                            .count(),
                    type
            );
        }
        // Measurement type with the most measurements for the event
        MeasurementType max = count.get(Collections.max(count.keySet()));
        // Set the drawable corresponding the measurement type with the most measurements
        thumbnail.setImageResource(max.getResId(context));
    }

    /**
     * Sets the elapsed days since an {@link Event event} happened.
     *
     * @param event The corresponding event
     */
    private void setupTime(GenericEvent event) {
        // Difference between now and the events time in milliseconds
        long differenceInMilliseconds = Calendar.getInstance().getTimeInMillis() - event.getTime();
        int differenceInDays = (int) (differenceInMilliseconds / (24 * 60 * 60 * 1000));
        if (differenceInDays == 0) {
            time.setText(context.getString(R.string.today));
        } else {
            time.setText(context.getResources().getQuantityString(
                    R.plurals.days_ago,
                    differenceInDays,
                    differenceInDays
            ));
        }
    }

    /**
     * Sets the distance from the current location to the events location.
     *
     * @param event The corresponding event
     */
    private void setupLocation(GenericEvent event) {
        LocationManager locationManager =
                (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            try {
                Location location = locationManager.getLastKnownLocation(provider);
                if (location == null) {
                    continue;
                }
                if (bestLocation == null || location.getAccuracy() < bestLocation.getAccuracy()) {
                    bestLocation = location;
                }
            } catch (SecurityException e) {
                // TODO Handle user did not grant permission
                e.printStackTrace();
            }
        }

        if (bestLocation != null) {
            location.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.distance_to),
                    // Distance in kilometers
                    bestLocation.distanceTo(event.getLocation()) / 1000
            ));
        }
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
}
