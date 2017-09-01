package de.uni_marburg.mathematik.ds.serval.controller;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import butterknife.BindView;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.model.Measurement;
import de.uni_marburg.mathematik.ds.serval.model.MeasurementType;
import de.uni_marburg.mathematik.ds.serval.model.exceptions.MeasurementTypeWithoutIcon;
import de.uni_marburg.mathematik.ds.serval.util.DataTypeConversionUtil;
import de.uni_marburg.mathematik.ds.serval.util.distance.Distance;
import de.uni_marburg.mathematik.ds.serval.util.distance.DistanceUnit;
import de.uni_marburg.mathematik.ds.serval.util.LocationUtil;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * ViewHolder for {@link GenericEvent generic events}
 */
class GenericEventViewHolder extends BaseViewHolder<GenericEvent> implements LocationListener {
    
    private static final float MINIMUM_DISTANCE_IN_METERS = 50.0f;
    
    private Context context;
    
    private Event event;
    
    private Location currentLocation;
    
    LocationManager locationManager;
    
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
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            // TODO Adapt view to no location permission granted
            return;
        }
        locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                TimeUnit.MILLISECONDS.toMinutes(1),
                MINIMUM_DISTANCE_IN_METERS,
                this
        );
    }
    
    @Override
    protected void onBind(GenericEvent event, int position) {
        this.event = event;
        setupTime();
        setupMeasurementIcons();
    }
    
    /**
     * Sets the elapsed days since an {@link Event event} happened.
     */
    private void setupTime() {
        // Difference between now and the events time in milliseconds
        long differenceInMilliseconds = Calendar.getInstance().getTimeInMillis() - event.getTime();
        long differenceInDays = TimeUnit.MILLISECONDS.toDays(differenceInMilliseconds);
        int differenceInDaysInteger;
        
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            differenceInDaysInteger = Math.toIntExact(differenceInDays);
        } else {
            differenceInDaysInteger = DataTypeConversionUtil.safeLongToInt(differenceInDays);
        }
        
        if (differenceInDaysInteger == 0) {
            time.setText(context.getString(R.string.today));
        } else {
            time.setText(context.getResources().getQuantityString(
                    R.plurals.days_ago,
                    differenceInDaysInteger,
                    differenceInDaysInteger
            ));
        }
    }
    
    /**
     * Loads icons for each measurement type available in the measurements of the event.
     *
     * @param event The corresponding event
     */
    private void setupMeasurementIcons() {
        measurementTypes.removeAllViews();
        Set<MeasurementType> types = new HashSet<>();
        
        if (VERSION.SDK_INT >= VERSION_CODES.N) {
            types = event.getMeasurements()
                         .stream()
                         .map(Measurement::getType)
                         .collect(Collectors.toSet());
        } else {
            for (Measurement measurement : event.getMeasurements()) {
                types.add(measurement.getType());
            }
        }
        
        for (MeasurementType type : types) {
            ImageView icon = new ImageView(context);
            try {
                icon.setImageResource(type.getResId(context));
                icon.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
                measurementTypes.addView(icon);
            } catch (MeasurementTypeWithoutIcon measurementTypeWithoutIcon) {
                measurementTypeWithoutIcon.printStackTrace();
            }
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
    
    @Override
    public void onLocationChanged(Location location) {
        if (LocationUtil.isBetterLocation(location, currentLocation)) {
            Distance kilometers = new Distance(
                    event.getLocation().distanceTo(location),
                    DistanceUnit.KILOMETER
            );
            this.location.setText(String.format(
                    context.getString(R.string.distance_to),
                    kilometers.toString()
            ));
        }
    }
    
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        
    }
    
    @Override
    public void onProviderEnabled(String s) {
        
    }
    
    @Override
    public void onProviderDisabled(String s) {
        
    }
}
