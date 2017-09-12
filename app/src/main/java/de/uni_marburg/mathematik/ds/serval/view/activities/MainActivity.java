package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.BuildConfig;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.controller.tasks.EventAsyncTask;
import de.uni_marburg.mathematik.ds.serval.model.comparators.LocationComparator;
import de.uni_marburg.mathematik.ds.serval.model.comparators.MeasurementsComparator;
import de.uni_marburg.mathematik.ds.serval.model.comparators.TimeComparator;
import de.uni_marburg.mathematik.ds.serval.model.event.Event;
import de.uni_marburg.mathematik.ds.serval.model.event.EventCallback;
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator;
import de.uni_marburg.mathematik.ds.serval.util.ChangelogUtil;
import de.uni_marburg.mathematik.ds.serval.util.LocationUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.fragments.DashboardFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.PlaceholderFragment;
import ru.noties.markwon.Markwon;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Main view of the app.
 * <p>
 * Currently shows a list of all events. Might be changed to a dashboard.
 */
public class MainActivity<T extends Event>
        extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener, EventCallback<T> {
    
    private static final int CHECK_LOCATION_PERMISSION = 42;
    
    private List<T> events;
    
    private Bundle savedInstanceState;
    
    public static Location lastLocation;
    
    private PrefManager prefManager;
    
    private FragmentManager fragmentManager;
    
    private FirebaseAnalytics firebaseAnalytics;
    
    private FusedLocationProviderClient fusedLocationProviderClient;
    
    private LocationRequest locationRequest;
    
    private LocationCallback locationCallback;
    
    private EventAsyncTask<T> eventAsyncTask;
    
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupFields(savedInstanceState);
        setupLocationUpdate();
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        if (prefManager.requestLocationUpdates()) {
            stopLocationUpdates();
        }
        prefManager.setBottomNavigationSelectedItemId(bottomNavigationView.getSelectedItemId());
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) == PERMISSION_GRANTED) {
            prefManager.setRequestLocationUpdates(true);
            startLocationUpdates();
        } else {
            prefManager.setRequestLocationUpdates(false);
        }
    }
    
    @Override
    public void onBackPressed() {
        if (prefManager.confirmExit()) {
            new MaterialDialog.Builder(this).title(R.string.confirm_exit)
                                            .positiveText(R.string.exit)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(
                                                        @NonNull MaterialDialog dialog,
                                                        @NonNull DialogAction which
                                                ) {
                                                    finish();
                                                }
                                            })
                                            .negativeText(R.string.cancel)
                                            .show();
        } else {
            finish();
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_show_changelog:
                checkForNewVersion(true);
                return true;
            case R.id.action_reset_app:
                prefManager.setIsFirstTimeLaunch(true);
                startActivity(new Intent(this, IntroActivity.class));
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content);
        
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if (!(currentFragment instanceof DashboardFragment)) {
                    fragment = new PlaceholderFragment();
                    firebaseAnalytics.setCurrentScreen(
                            this,
                            getString(R.string.screen_dashboard),
                            null
                    );
                }
                break;
            case R.id.action_events:
                if (!(currentFragment instanceof EventsFragment)) {
                    fragment = new EventsFragment();
                    firebaseAnalytics.setCurrentScreen(
                            this,
                            getString(R.string.screen_events),
                            null
                    );
                }
                break;
            case R.id.action_map:
                if (!(currentFragment instanceof MapFragment)) {
                    fragment = new MapFragment<>();
                    firebaseAnalytics.setCurrentScreen(
                            this,
                            getString(R.string.screen_map),
                            null
                    );
                }
                break;
            default:
                fragment = new PlaceholderFragment();
        }
        
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            return true;
        }
        
        return false;
    }
    
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        switch (requestCode) {
            case CHECK_LOCATION_PERMISSION:
                if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prefManager.setRequestLocationUpdates(true);
                    startLocationUpdates();
                } else {
                    prefManager.setRequestLocationUpdates(false);
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    @Override
    public void onEventsLoaded(List<T> events) {
        this.events = events;
        // Update UI
        setupViews(savedInstanceState);
        checkForNewVersion(false);
    }
    
    @Override
    public List<T> onEventsRequested(EventComparator comparator, boolean reversed, int count) {
        switch (comparator) {
            case DISTANCE:
                if (lastLocation != null) {
                    if (reversed) {
                        Collections.sort(events, new LocationComparator<>(lastLocation));
                        Collections.reverse(events);
                    } else {
                        Collections.sort(events, new LocationComparator<>(lastLocation));
                    }
                }
                break;
            case MEASUREMENTS:
                if (reversed) {
                    Collections.sort(events, new MeasurementsComparator<>());
                    Collections.reverse(events);
                } else {
                    Collections.sort(events, new MeasurementsComparator<>());
                }
                break;
            case SHUFFLE:
                Collections.shuffle(events);
                break;
            case TIME:
                if (reversed) {
                    Collections.sort(events, new TimeComparator<>());
                    Collections.reverse(events);
                } else {
                    Collections.sort(events, new TimeComparator<>());
                }
                break;
        }
        return new ArrayList<>(events.subList(0, Math.min(events.size(), count)));
    }
    
    private void setupFields(Bundle savedInstanceState) {
        this.savedInstanceState = savedInstanceState;
        events = new ArrayList<>();
        eventAsyncTask = new EventAsyncTask<>(this);
        prefManager = new PrefManager(this);
        fragmentManager = getSupportFragmentManager();
        firebaseAnalytics = Serval.getFirebaseAnalytics(this);
        loadEvents();
    }
    
    private void loadEvents() {
        eventAsyncTask.execute(getString(R.string.url_rest_api));
    }
    
    private void setupLocationUpdate() {
        if (prefManager.requestLocationUpdates()) {
            fusedLocationProviderClient = new FusedLocationProviderClient(this);
            locationRequest = new LocationRequest();
            locationRequest.setInterval(TimeUnit.SECONDS.toMillis(60));
            locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(5));
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    Location location = locationResult.getLastLocation();
                    if (LocationUtil.isBetterLocation(location, lastLocation)) {
                        lastLocation = location;
                    }
                }
            };
        }
    }
    
    private void setupViews(Bundle savedInstanceState) {
        setSupportActionBar(toolbar);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (savedInstanceState == null) {
            transaction.replace(R.id.content, new PlaceholderFragment());
        } else {
            switch (prefManager.getBottomNavigationSelectedItemId()) {
                case R.id.action_dashboard:
                    transaction.replace(R.id.content, new PlaceholderFragment());
                    break;
                case R.id.action_events:
                    transaction.replace(R.id.content, new EventsFragment());
                    break;
                case R.id.action_map:
                    transaction.replace(R.id.content, new MapFragment<>());
                    break;
                default:
                    transaction.replace(R.id.content, new PlaceholderFragment());
            }
        }
        transaction.commit();
    }
    
    private void checkForNewVersion(boolean force) {
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int lastKnownVersionCode = prefManager.getLastKnownVersionCode();
            if (force || prefManager.showChangelog() && lastKnownVersionCode < versionCode) {
                showChangelog(versionCode);
                prefManager.setLastKnownVersionCode(versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            Crashlytics.logException(e);
        }
    }
    
    private void showChangelog(int versionCode) {
        String versionName = String.format(
                Locale.getDefault(),
                getString(R.string.changelog),
                BuildConfig.VERSION_NAME
        );
        String changelog = ChangelogUtil.readChangelogFromAsset(this, versionCode);
        if (prefManager.useBottomSheetDialogs()) {
            showChangelogBottomSheetDialog(versionName, changelog);
        } else {
            showChangelogDialog(versionName, changelog);
        }
    }
    
    @SuppressLint("InflateParams")
    private void showChangelogBottomSheetDialog(String versionName, String changelog) {
        View view = LayoutInflater.from(this).inflate(R.layout.changelog_bottom_sheet_dialog, null);
        TextView version = view.findViewById(R.id.version);
        version.setText(versionName);
        TextView changelogView = view.findViewById(R.id.changelog);
        Markwon.setMarkdown(changelogView, changelog);
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        dialog.setContentView(view);
        dialog.show();
    }
    
    private void showChangelogDialog(String versionName, String changelog) {
        TextView content = new TextView(this);
        Markwon.setMarkdown(content, changelog);
        new MaterialDialog.Builder(this).title(versionName)
                                        .customView(content, true)
                                        .positiveText(android.R.string.ok)
                                        .show();
    }
    
    private void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions();
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.myLooper()
        );
    }
    
    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
    
    private void requestPermissions() {
        ActivityCompat.requestPermissions(
                this,
                new String[]{ACCESS_FINE_LOCATION},
                CHECK_LOCATION_PERMISSION
        );
    }
}
