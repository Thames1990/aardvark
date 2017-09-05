package de.uni_marburg.mathematik.ds.serval.controller.view_holders;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.support.annotation.LayoutRes;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;

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
public class GenericEventViewHolder extends BaseViewHolder<GenericEvent> {
    
    private Context context;
    
    private Event event;
    
    @BindView(R.id.undo)
    public Button undo;
    @BindView(R.id.measurement_types)
    public LinearLayout measurementTypes;
    @BindView(R.id.time)
    public TextView time;
    @BindView(R.id.location)
    public TextView location;
    
    public GenericEventViewHolder(ViewGroup parent, @LayoutRes int itemLayoutId) {
        super(parent, itemLayoutId);
        context = parent.getContext();
    }
    
    @Override
    protected void onBind(GenericEvent event, int position) {
        this.event = event;
        setupTime();
        requestLocation();
        setupMeasurementIcons();
    }
    
    /**
     * Sets the elapsed days since an {@link Event event} happened.
     */
    private void setupTime() {
        Calendar calendar = Calendar.getInstance();
        DateFormat format =
                SimpleDateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        // Difference between now and the events time in milliseconds
        long difference = calendar.getTimeInMillis() - event.getTime();
        if (TimeUnit.MILLISECONDS.toMinutes(difference) < 60) {
            time.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_minutes_ago),
                    TimeUnit.MILLISECONDS.toMinutes(difference)
            ));
        } else if (TimeUnit.MILLISECONDS.toHours(difference) < 24) {
            time.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_hours_ago),
                    TimeUnit.MILLISECONDS.toHours(difference)
            ));
        } else if (TimeUnit.MILLISECONDS.toDays(difference) < 7) {
            time.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.time_days_ago),
                    TimeUnit.MILLISECONDS.toDays(difference)
            ));
        } else {
            time.setText(format.format(calendar.getTime()));
        }
        
        format.format(calendar.getTime());
    }
    
    private void requestLocation() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.MINUTES.toMillis(15));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(75));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        settingsClient.checkLocationSettings(locationSettingsRequest);
        
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }
        FusedLocationProviderClient client =
                LocationServices.getFusedLocationProviderClient(context);
        LocationCallback callback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                setupLocation(locationResult.getLastLocation());
                client.removeLocationUpdates(this);
            }
        };
        client.requestLocationUpdates(
                locationRequest,
                callback,
                null
        );
    }
    
    private void setupLocation(Location newLocation) {
        float distanceInMeters = newLocation.distanceTo(event.getLocation());
        
        if (distanceInMeters < 1000) {
            location.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.location_distance_to_meter),
                    distanceInMeters
            ));
        } else {
            location.setText(String.format(
                    Locale.getDefault(),
                    context.getString(R.string.location_distance_to_kilometer),
                    distanceInMeters / 1000
            ));
        }
    }
    
    /**
     * Loads icons for each measurement type available in the measurements of the event.
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
            icon.setImageResource(type.getResId(context));
            icon.setLayoutParams(new LinearLayout.LayoutParams(WRAP_CONTENT, MATCH_PARENT));
            measurementTypes.addView(icon);
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
