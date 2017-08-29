package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.DetailAdapter;
import de.uni_marburg.mathematik.ds.serval.model.Event;

/**
 * Is used to display all informations about an {@link Event event}.
 */
public class DetailActivity<T extends Event> extends AppCompatActivity {

    /**
     * Is used as a key to pass the {@link DetailActivity#event event} between the
     * {@link MainActivity main activity} and this view.
     */
    public static final String EVENT = "EVENT";

    /**
     * Event to show details for
     */
    private T event;

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        event = getIntent().getExtras().getParcelable(EVENT);
        setupViews();
    }

    private void setupViews() {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.details));
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
        viewPager.setAdapter(new DetailAdapter<>(getSupportFragmentManager(), event, this));
        tabLayout.setupWithViewPager(viewPager);
        fab.setOnClickListener(view -> navigateToPosition());
    }

    /**
     * Opens Google Maps and navigates to the position of the event
     */
    private void navigateToPosition() {
        Location location = event.getLocation();
        Intent navigationIntent = new Intent(
                Intent.ACTION_VIEW,
                Uri.parse(String.format(
                        Locale.getDefault(),
                        getString(R.string.intent_uri_navigate),
                        // Forces decimal points
                        String.format(Locale.ENGLISH, "%.5f", location.getLatitude()),
                        String.format(Locale.ENGLISH, "%.5f", location.getLongitude())
                ))
        );
        navigationIntent.setPackage("com.google.android.apps.maps");
        startActivity(navigationIntent);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
