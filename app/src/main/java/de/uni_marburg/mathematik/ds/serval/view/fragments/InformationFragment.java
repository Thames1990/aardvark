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
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.model.Event;
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

    /**
     * Event to show information about
     */
    private T event;

    @BindView(R.id.map)
    MapView map;
    @BindView(R.id.info)
    TextView info;

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
                    getString(R.string.fragment_must_contain_key_exception),
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
        ButterKnife.bind(this, view);
        map.onCreate(getArguments());
        map.getMapAsync(this);
        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Location location = event.getLocation();
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();
        LatLng position = new LatLng(latitude, longitude);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.addMarker(new MarkerOptions().position(position));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, 15.0f));
        map.onResume();
        // TODO Add better information
        info.setText("Lat: " + latitude + ", Lon: " + longitude);
    }
}
