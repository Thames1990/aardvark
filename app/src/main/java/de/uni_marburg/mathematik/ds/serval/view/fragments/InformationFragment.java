package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.leakcanary.RefWatcher;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity;

/**
 * Shows all information about an {@link Event event} except for the measurements.
 */
public class InformationFragment<T extends Event> extends Fragment implements OnMapReadyCallback {
    
    /**
     * This key is used to collect the {@link InformationFragment#event event} from the
     * {@link DetailActivity detail activity}.
     */
    public static final String EVENT = "EVENT";
    
    private static final float ZOOM_LEVEL = 15.0f;
    
    /**
     * Event to show information about
     */
    private T event;
    
    private Unbinder unbinder;
    
    private GoogleMap googleMap;
    
    @BindView(R.id.map)
    MapView map;
    
    public static <T extends Event> InformationFragment<Event> newInstance(T event) {
        InformationFragment<Event> fragment = new InformationFragment<>();
        Bundle args = new Bundle();
        args.putParcelable(EVENT, event);
        fragment.setArguments(args);
        return fragment;
    }
    
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        if (!getArguments().containsKey(EVENT)) {
            throw new RuntimeException(String.format(
                    Locale.getDefault(),
                    getString(R.string.exception_fragment_must_contain_key),
                    EVENT
            ));
        }
        event = getArguments().getParcelable(EVENT);
    }
    
    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState
    ) {
        View view = inflater.inflate(R.layout.fragment_information, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MapsInitializer.initialize(getActivity());
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
        addEventLocation();
    }
    
    private void setupGoogleMap() {
        UiSettings settings = googleMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setMapToolbarEnabled(false);
    }
    
    private void addEventLocation() {
        Location location = event.getLocation();
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(position));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
    }
}
