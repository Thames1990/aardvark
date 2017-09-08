package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.ExtendedInfoWindowAdapter;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * TODO Add JavaDoc
 *
 * @param <T>
 */
public class MapFragment<T extends Event>
        extends SupportMapFragment
        implements OnInfoWindowClickListener, OnMapReadyCallback, OnMyLocationButtonClickListener {
    
    private static final int CHECK_LOCATION_PERMISSION = 42;
    
    private static final int MAP_PADDING = 200;
    
    private static final int EVENT_COUNT = 50;
    
    private List<T> events;
    
    private GoogleMap googleMap;
    
    private Location lastLocation;
    
    private HashMap<Marker, T> markerEventMap;
    
    private PrefManager prefManager;
    
    private EventCallback<T> eventCallback;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFields();
        requestEvents(EventComparator.DISTANCE, false, EVENT_COUNT);
        getMapAsync(this);
    }
    
    @Override
    public View onCreateView(
            LayoutInflater layoutInflater, ViewGroup viewGroup, Bundle bundle
    ) {
        requestEvents(EventComparator.DISTANCE, false, EVENT_COUNT);
        return super.onCreateView(layoutInflater, viewGroup, bundle);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupGoogleMap();
        addEventLocations();
        zoomToFitMarkers(false);
    }
    
    @Override
    public void onInfoWindowClick(Marker marker) {
        T event = markerEventMap.get(marker);
        Intent eventIntent = new Intent(getActivity(), DetailActivity.class);
        eventIntent.putExtra(DetailActivity.EVENT, event);
        startActivity(eventIntent);
    }
    
    @Override
    public boolean onMyLocationButtonClick() {
        zoomToFitMarkers(true);
        return true;
    }
    
    private void setupFields() {
        //noinspection unchecked
        eventCallback = (EventCallback<T>) getActivity();
        lastLocation = ((MainActivity) getActivity()).getLastLocation();
        markerEventMap = new HashMap<>();
        prefManager = new PrefManager(getContext());
    }
    
    private void setupGoogleMap() {
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(new ExtendedInfoWindowAdapter(getContext()));
        if (prefManager.requestLocationUpdates()) {
            if (checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
                requestPermissions();
                return;
            }
            googleMap.setMyLocationEnabled(true);
            googleMap.setOnMyLocationButtonClickListener(this);
        }
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{ACCESS_FINE_LOCATION},
                CHECK_LOCATION_PERMISSION
        );
    }
    
    private void addEventLocations() {
        Calendar calendar = Calendar.getInstance();
        DateFormat format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.LONG,
                DateFormat.SHORT,
                Locale.getDefault()
        );
        for (T event : events) {
            Location location = event.getLocation();
            LatLng position = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );
            calendar.setTimeInMillis(event.getTime());
            Marker marker = googleMap.addMarker(
                    new MarkerOptions().position(position)
                                       .title(getString(R.string.event))
                                       .snippet(format.format(calendar.getTime()))
                                       .icon(ImageUtil.getBitmapDescriptor(
                                               R.drawable.event,
                                               getContext()
                                       ))
            );
            markerEventMap.put(marker, event);
        }
    }
    
    private void zoomToFitMarkers(boolean animate) {
        CameraUpdate update = null;
        LatLng lastLocationPosition = null;
        
        if (lastLocation != null) {
            lastLocationPosition = new LatLng(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );
        }
        
        if (!markerEventMap.isEmpty()) {
            // Events are available
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            
            for (Marker marker : markerEventMap.keySet()) {
                builder.include(marker.getPosition());
            }
            
            if (lastLocationPosition != null) {
                // Last position is also available
                builder.include(lastLocationPosition);
            }
            
            update = CameraUpdateFactory.newLatLngBounds(builder.build(), MAP_PADDING);
        } else if (lastLocationPosition != null) {
            // No events are available, but the last location
            update = CameraUpdateFactory.newLatLng(lastLocationPosition);
        }
        
        // Zoom in/out camera to include events and/or the last location on availibility
        if (update != null) {
            if (animate) {
                googleMap.animateCamera(update);
            } else {
                googleMap.moveCamera(update);
            }
        }
    }
    
    protected void requestEvents(EventComparator comparator, boolean reversed, int count) {
        events = eventCallback.onEventsRequested(comparator, reversed, count);
    }
}
