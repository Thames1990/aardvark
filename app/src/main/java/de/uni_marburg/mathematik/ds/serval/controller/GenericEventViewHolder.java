package de.uni_marburg.mathematik.ds.serval.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.model.Measurement;
import de.uni_marburg.mathematik.ds.serval.model.MeasurementType;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

/**
 * ViewHolder for {@link GenericEvent generic events}
 */
class GenericEventViewHolder extends BaseViewHolder<GenericEvent> {

    private Context context;

    @BindView(R.id.measurement_types)
    LinearLayout measurementTypes;

    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.location)
    TextView location;

    GenericEventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
        context = parent.getContext();
    }

    @Override
    protected void onBind(GenericEvent event, int position) {
        setupTime(event);
        setupLocation(event);
        setupMeasurementIcons(event);
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

    /**
     * Loads icons for each measurement type available in the measurement of the event.
     *
     * @param event The corresponding event
     */
    private void setupMeasurementIcons(GenericEvent event) {
        Set<MeasurementType> types = new HashSet<>();

        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            types = event.getMeasurements().stream()
                    .map(Measurement::getType)
                    .collect(Collectors.toSet());
        } else {
            for (Measurement measurement : event.getMeasurements()) {
                types.add(measurement.getType());
            }
        }

        for (MeasurementType type : types) {
            ImageView view = new ImageView(context);
            view.setImageResource(type.getResId(context));
            view.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            ));
            measurementTypes.addView(view, measurementTypes.getChildCount());
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
