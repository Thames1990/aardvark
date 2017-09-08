package de.uni_marburg.mathematik.ds.serval.view.fragments;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
    
    private SupportMapFragment map;
    
    @BindView(R.id.time_value)
    TextView time;
    @BindView(R.id.latitude_value)
    TextView latitude;
    @BindView(R.id.longitude_value)
    TextView longitude;
    @BindView(R.id.geohash_value)
    TextView geohash;
    @BindView(R.id.measurements_value)
    TextView measurements;
    
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
        map = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }
    
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setupViews();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        unbinder.unbind();
        Serval.getRefWatcher(getActivity()).watch(this);
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
    
    private void setupViews() {
        map.getMapAsync(this);
        DateFormat format = SimpleDateFormat.getDateTimeInstance(
                DateFormat.MEDIUM,
                DateFormat.MEDIUM,
                Locale.getDefault()
        );
        time.setText(format.format(event.getTime()));
        Location location = event.getLocation();
        latitude.setText(String.valueOf(location.getLatitude()));
        longitude.setText(String.valueOf(location.getLongitude()));
        geohash.setText(event.getGeohashLocation().getGeohash());
        measurements.setText(String.valueOf(event.getMeasurements().size()));
    }
    
    private void addEventLocation() {
        Location location = event.getLocation();
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(position));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
    }
}
