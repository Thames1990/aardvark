package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemInfoWindowClickListener;

import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.ExtendedInfoWindowAdapter;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
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
        extends EventFragment<T>
        implements OnClusterItemInfoWindowClickListener<T>, OnClusterClickListener<T>,
                   OnMapReadyCallback, OnMyLocationButtonClickListener {
    
    private static final int CHECK_LOCATION_PERMISSION = 42;
    
    private static final int MAP_PADDING = 50;
    
    /**
     * 1: World
     * 5: Continent
     * 10: City
     * 15: Streets
     * 20: Buildings
     */
    private static final float MAP_ZOOM = 15;
    
    private ClusterManager<T> clusterManager;
    
    private GoogleMap googleMap;
    
    private Location lastLocation;
    
    private PrefManager prefManager;
    
    private SupportMapFragment map;
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupFields();
        map.getMapAsync(this);
    }
    
    @Override
    protected int getLayout() {
        return R.layout.fragment_map;
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_map, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_change_map_type_hybrid:
                googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.action_change_map_type_none:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                return true;
            case R.id.action_change_map_type_normal:
                googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.action_change_map_type_satellite:
                googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            case R.id.action_change_map_type_terrain:
                googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupGoogleMap();
        if (prefManager.requestLocationUpdates()) {
            moveCameraToLastLocation(false);
        }
    }
    
    @Override
    public boolean onMyLocationButtonClick() {
        moveCameraToLastLocation(true);
        return true;
    }
    
    @Override
    public boolean onClusterClick(Cluster<T> cluster) {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (ClusterItem clusterItem : cluster.getItems()) {
            builder.include(clusterItem.getPosition());
        }
        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), MAP_PADDING));
        return true;
    }
    
    @Override
    public void onClusterItemInfoWindowClick(T event) {
        Intent eventIntent = new Intent(getActivity(), DetailActivity.class);
        eventIntent.putExtra(DetailActivity.EVENT, event);
        startActivity(eventIntent);
    }
    
    private void setupFields() {
        //noinspection unchecked
        eventCallback = (EventCallback<T>) getActivity();
        lastLocation = ((MainActivity) getActivity()).getLastLocation();
        prefManager = new PrefManager(getContext());
    }
    
    private void setupGoogleMap() {
        setupClusterManager();
        setupGoogleMapsListeners(clusterManager);
        setupCameraBounds();
        setupMyLocation();
    }
    
    private void setupClusterManager() {
        clusterManager = new ClusterManager<>(getContext(), googleMap);
        clusterManager.setOnClusterClickListener(this);
        clusterManager.setOnClusterItemInfoWindowClickListener(this);
        clusterManager.addItems(events);
        clusterManager.cluster();
    }
    
    private void setupGoogleMapsListeners(ClusterManager<T> clusterManager) {
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);
        googleMap.setOnInfoWindowClickListener(clusterManager);
        googleMap.setInfoWindowAdapter(new ExtendedInfoWindowAdapter(getContext()));
    }
    
    private void setupCameraBounds() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        for (T event : events) {
            builder.include(event.getPosition());
        }
        googleMap.setLatLngBoundsForCameraTarget(builder.build());
    }
    
    private void setupMyLocation() {
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
    
    private void moveCameraToLastLocation(boolean animate) {
        if (lastLocation != null) {
            LatLng lastLocationPosition = new LatLng(
                    lastLocation.getLatitude(),
                    lastLocation.getLongitude()
            );
            CameraUpdate update = CameraUpdateFactory.newLatLngZoom(lastLocationPosition, MAP_ZOOM);
            if (animate) {
                googleMap.animateCamera(update);
            } else {
                googleMap.moveCamera(update);
            }
        }
    }
}
