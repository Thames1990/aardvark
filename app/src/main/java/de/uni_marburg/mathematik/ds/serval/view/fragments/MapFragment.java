package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.leakcanary.RefWatcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil;
import de.uni_marburg.mathematik.ds.serval.util.LocationUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;
import de.uni_marburg.mathematik.ds.serval.view.util.ExtendedInfoWindowAdapter;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.content.ContextCompat.checkSelfPermission;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

/**
 * Created by thames1990 on 28.08.17.
 * TODO Use Google Maps Fragment
 */
public class MapFragment<T extends Event>
        extends Fragment
        implements OnInfoWindowClickListener, OnMapReadyCallback, OnMyLocationButtonClickListener {
    
    public static final String EVENTS = "EVENTS";
    
    private static final int CHECK_LOCATION_PERMISSION = 0;
    
    // TODO Export to PrefManager
    private static final int MAP_PADDING = 200;
    
    private boolean requestingLocationUpdates;
    
    private List<T> events;
    
    private GoogleMap googleMap;
    
    private HashMap<Marker, T> markerEventMap;
    
    private Unbinder unbinder;
    
    private Location lastLocation;
    
    @BindView(R.id.map)
    MapView map;
    
    public static <T extends Event> MapFragment newInstance(ArrayList<T> events) {
        MapFragment<Event> fragment = new MapFragment<>();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS, events);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        
        if (!getArguments().containsKey(EVENTS)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    EVENTS
            ));
        }
        events = getArguments().getParcelableArrayList(EVENTS);
        markerEventMap = new HashMap<>();
        requestingLocationUpdates = new PrefManager(getContext()).requestLocationUpdates();
    }
    
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        map.onCreate(savedInstanceState);
        map.getMapAsync(this);
    }
    
    @Override
    public void onStart() {
        super.onStart();
        map.onStart();
    }
    
    @Override
    public void onResume() {
        super.onResume();
        map.onResume();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    
    @Override
    public void onPause() {
        super.onPause();
        map.onPause();
    }
    
    @Override
    public void onStop() {
        super.onStop();
        map.onStop();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        map.onDestroy();
        unbinder.unbind();
        RefWatcher refWatcher = Serval.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        map.onSaveInstanceState(outState);
    }
    
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        map.onLowMemory();
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupGoogleMap();
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
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
    
    private void setupGoogleMap() {
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(new ExtendedInfoWindowAdapter(getContext()));
        googleMap.setOnMyLocationButtonClickListener(this);
        if (checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        googleMap.setMyLocationEnabled(true);
    }
    
    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(60));
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5));
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();
        
        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);
        
        if (checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        getFusedLocationProviderClient(getActivity()).requestLocationUpdates(
                locationRequest,
                new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        onLocationChanged(locationResult.getLastLocation());
                    }
                },
                Looper.myLooper()
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
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerEventMap.keySet()) {
            builder.include(marker.getPosition());
        }
        if (lastLocation != null) {
            LatLng position = new LatLng(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );
            builder.include(position);
        }
        LatLngBounds bounds = builder.build();
        
        CameraUpdate update =
                CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING);
        
        if (animate) {
            googleMap.animateCamera(update);
        } else {
            googleMap.moveCamera(update);
        }
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                getActivity(),
                new String[]{ACCESS_FINE_LOCATION},
                CHECK_LOCATION_PERMISSION
        );
        // TODO Check if user explicitly denied the permission
        if (requestingLocationUpdates) {
            startLocationUpdates();
        }
    }
    
    private void onLocationChanged(Location location) {
        if (LocationUtil.isBetterLocation(location, lastLocation)) {
            lastLocation = location;
        }
    }
}
