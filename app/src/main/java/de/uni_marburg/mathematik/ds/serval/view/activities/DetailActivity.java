package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.MeasurementsAdapter;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;

/**
 * Displays all details of an {@link Event event}.
 *
 * @param <T> Type of the {@link Event event}
 */
public class DetailActivity<T extends Event>
        extends AppCompatActivity
        implements OnMapReadyCallback, AppBarLayout.OnOffsetChangedListener {
    
    /**
     * Is used as a key to pass the {@link DetailActivity#event event} between the
     * {@link MainActivity main activity} and this view.
     */
    public static final String EVENT = "EVENT";
    
    /**
     * Zoom level of the {@link DetailActivity#googleMap map}
     */
    private static final float ZOOM_LEVEL = 15f;
    
    /**
     * Event to show details for
     */
    private T event;
    
    private GoogleMap googleMap;
    
    private boolean isShown = true;
    
    private int scrollRange = -1;
    
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.appbar_layout)
    AppBarLayout appBarLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.collapsingToolbarLayout)
    CollapsingToolbarLayout collapsingToolbarLayout;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        event = getIntent().getExtras().getParcelable(EVENT);
        setupViews();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        setupGoogleMap();
    }
    
    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        if (scrollRange == -1) {
            scrollRange = appBarLayout.getTotalScrollRange();
        }
        
        if (scrollRange + verticalOffset == 0) {
            collapsingToolbarLayout.setTitle(getString(R.string.details));
            isShown = true;
        } else if (isShown) {
            collapsingToolbarLayout.setTitle(" ");
            isShown = false;
        }
    }
    
    /**
     * Sets up all {@link android.view.View views}.
     */
    private void setupViews() {
        setupToolbar();
        setupRecyclerView();
        setupMap();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInGoogleMaps();
            }
        });
    }
    
    private void setupToolbar() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.details));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        appBarLayout.addOnOffsetChangedListener(this);
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(
                this,
                DividerItemDecoration.VERTICAL
        ));
        recyclerView.setAdapter(new MeasurementsAdapter(event.getMeasurements()));
    }
    
    private void setupMap() {
        GoogleMapOptions options = new GoogleMapOptions().liteMode(true);
        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        getSupportFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);
    }
    
    private void setupGoogleMap() {
        UiSettings settings = googleMap.getUiSettings();
        settings.setAllGesturesEnabled(false);
        settings.setMapToolbarEnabled(false);
        addEventLocation(googleMap);
        // TODO Figure out how to disable Google Maps Intent on click
    }
    
    private void addEventLocation(GoogleMap googleMap) {
        Location location = event.getLocation();
        LatLng position = new LatLng(location.getLatitude(), location.getLongitude());
        googleMap.addMarker(new MarkerOptions().position(position));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(position, ZOOM_LEVEL));
    }
    
    private void showInGoogleMaps() {
        Location location = event.getLocation();
        Intent navigationIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(String.format(
                        getString(R.string.intent_uri_show_in_google_maps),
                        // Forces decimal points
                        String.format(Locale.ENGLISH, "%.5f", location.getLatitude()),
                        String.format(Locale.ENGLISH, "%.5f", location.getLongitude()),
                        event.getTitle()
                ))
        );
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }
}
