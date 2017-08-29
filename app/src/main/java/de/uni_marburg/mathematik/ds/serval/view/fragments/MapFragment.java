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

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.Event;
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity;
import de.uni_marburg.mathematik.ds.serval.view.util.ExtendedInfoWindowAdapter;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by thames1990 on 28.08.17.
 */
public class MapFragment<T extends Event>
        extends Fragment
        implements OnInfoWindowClickListener, OnMapReadyCallback, OnSuccessListener<Location> {

    public static final String EVENTS = "EVENTS";

    private static final int MAP_PADDING = 200;

    /**
     * 10 seconds
     */
    private static final long UPDATE_INTERVAL = 10 * 1000;

    /**
     * 2 seconds
     */
    private static final long FASTEST_INTERVAL = 2000;

    private List<T> events;

    private LocationRequest locationRequest;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private GoogleMap googleMap;

    private HashMap<Marker, T> markerEventMap;

    private Marker currentPositionMarker;

    @BindView(R.id.map)
    MapView map;

    public static <T extends Event> MapFragment<Event> newInstance(ArrayList<T> events) {
        MapFragment<Event> fragment = new MapFragment<>();
        Bundle args = new Bundle();
        args.putParcelableArrayList(EVENTS, events);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!getArguments().containsKey(EVENTS)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.fragment_must_contain_key_exception),
                    EVENTS
            ));
        }
        events = getArguments().getParcelableArrayList(EVENTS);
        startLocationUpdates();
        fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(getActivity());
        markerEventMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        map.onCreate(getArguments());
        map.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupGoogleMap();
        getLastLocation();
        addEventLocations();
        zoomToFitMarkers();
        map.onResume();
    }

    private void setupGoogleMap() {
        googleMap.setOnInfoWindowClickListener(this);
        googleMap.setInfoWindowAdapter(new ExtendedInfoWindowAdapter(getContext()));
    }

    private void startLocationUpdates() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(locationRequest);
        LocationSettingsRequest locationSettingsRequest = builder.build();

        SettingsClient settingsClient = LocationServices.getSettingsClient(getActivity());
        settingsClient.checkLocationSettings(locationSettingsRequest);

        if (checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
                checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        LocationServices.getFusedLocationProviderClient(getContext()).requestLocationUpdates
                (locationRequest, new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                onLocationChanged(locationResult.getLastLocation());
                            }
                        },
                        Looper.myLooper());
    }

    private void requestPermissions() {
        ActivityCompat.requestPermissions(getActivity(), new String[]{
                ACCESS_COARSE_LOCATION,
                ACCESS_FINE_LOCATION
        }, MainActivity.CHECK_LOCATION_PERMISSION);
    }

    private void onLocationChanged(Location location) {
        LatLng position = new LatLng(
                location.getLatitude(),
                location.getLongitude()
        );
        if (currentPositionMarker != null) {
            currentPositionMarker.setPosition(position);
        } else {
            currentPositionMarker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Current location")
            );
        }

        markerEventMap.put(currentPositionMarker, null);
        zoomToFitMarkers();
    }

    public void getLastLocation() {
        if (checkSelfPermission(getContext(), ACCESS_FINE_LOCATION) != PERMISSION_GRANTED &&
                checkSelfPermission(getContext(), ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this);
    }

    private void addEventLocations() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat(
                getString(R.string.format_date_time),
                Locale.getDefault()
        );
        for (T event : events) {
            Location location = event.getLocation();
            LatLng position = new LatLng(
                    location.getLatitude(),
                    location.getLongitude()
            );
            calendar.setTimeInMillis(event.getTime());
            Marker marker = googleMap.addMarker(new MarkerOptions()
                    .position(position)
                    .title("Event")
                    .snippet(format.format(calendar.getTime()))
                    .icon(ImageUtil.getBitmapDescriptor(R.drawable.event, getContext()))
            );
            markerEventMap.put(marker, event);
        }
    }

    private void zoomToFitMarkers() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (Marker marker : markerEventMap.keySet()) {
            builder.include(marker.getPosition());
        }
        LatLngBounds bounds = builder.build();
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING));
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        T event = markerEventMap.get(marker);
        // Not current position
        if (event != null) {
            Intent eventIntent = new Intent(getActivity(), DetailActivity.class);
            eventIntent.putExtra(DetailActivity.EVENT, event);
            startActivity(eventIntent);
        }
    }

    @Override
    public void onSuccess(Location location) {
        if (location != null) {
            onLocationChanged(location);
        }
    }
}
