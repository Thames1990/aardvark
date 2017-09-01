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
class GenericEventViewHolder extends BaseViewHolder<GenericEvent> {
    
    private Context context;
    
    private Event event;
    
    private LocationManager locationManager;
    
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
    }
    
    @Override
    protected void onBind(GenericEvent event, int position) {
        this.event = event;
        title.setText(context.getString(R.string.title));
        setupTime();
        setupLocation();
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
    
    public void setupLocation() {
        if (checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            return;
        }
        Location lastKnownLocation =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            float distanceInMeters = lastKnownLocation.distanceTo(event.getLocation());
            
            if (distanceInMeters < 1000) {
                location.setText(String.format(
                        context.getString(R.string.distance_to_meter),
                        distanceInMeters
                ));
            } else {
                location.setText(String.format(
                        context.getString(R.string.distance_to_kilometer),
                        distanceInMeters / 1000
                ));
            }
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
}
