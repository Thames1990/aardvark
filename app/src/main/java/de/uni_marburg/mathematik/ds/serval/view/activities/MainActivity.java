package de.uni_marburg.mathematik.ds.serval.view.activities;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.util.LocationUtil;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.fragments.DashboardFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.EventsFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.MapFragment;
import de.uni_marburg.mathematik.ds.serval.view.fragments.PlaceholderFragment;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import us.feras.mdv.MarkdownView;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Main view of the app.
 * <p>
 * Currently shows a list of all events. Might be changed to a dashboard.
 */
public class MainActivity
        extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    
    private static final int CHECK_LOCATION_PERMISSION = 42;
    
    private static ArrayList<GenericEvent> events;
    
    private static Location lastLocation;
    
    private PrefManager prefManager;
    
    private FragmentManager fragmentManager;
    
    private FirebaseAnalytics firebaseAnalytics;
    
    private OkHttpClient okHttpClient;
    
    private FusedLocationProviderClient fusedLocationProviderClient;
    
    private LocationRequest locationRequest;
    
    private LocationCallback locationCallback;
    
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupFields();
        loadData();
        loadView(savedInstanceState);
        checkForNewVersion();
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_reset_app:
                prefManager.setIsFirstTimeLaunch(true);
                startActivity(new Intent(this, IntroActivity.class));
                finish();
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onBackPressed() {
        new MaterialDialog.Builder(this).title(R.string.confirm_exit)
                                        .positiveText(R.string.exit)
                                        .onPositive((dialog, which) -> finish())
                                        .negativeText(R.string.cancel)
                                        .show();
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content);
        
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if (!(currentFragment instanceof DashboardFragment)) {
                    fragment = new DashboardFragment();
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
                    fragment = new MapFragment();
                    firebaseAnalytics.setCurrentScreen(
                            this,
                            getString(R.string.screen_map),
                            null
                    );
                }
                break;
            default:
                fragment = PlaceholderFragment.newInstance();
        }
    
        if (fragment != null) {
            fragmentManager.beginTransaction().replace(R.id.content, fragment).commit();
            return true;
        }
        
        return false;
    }
    
    public static List<GenericEvent> getEvents(int count) {
        // TODO Based on distance
        Collections.sort(events, (event1, event2) -> (int) (event2.getTime() - event1.getTime()));
        return new ArrayList<>(events.subList(0, Math.min(events.size(), count)));
    }
    
    public static Location getLastLocation() {
        return lastLocation;
    }
    
    private void loadData() {
        events = new ArrayList<>();
        Request request = new Request.Builder().url(getString(R.string.url_rest_api)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(
                        getClass().getSimpleName(),
                        String.format(
                                getString(R.string.exception_load_data_failed),
                                e.getMessage()
                        )
                );
            }
            
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException();
                }
                
                Moshi moshi = new Moshi.Builder().build();
                JsonAdapter<GenericEvent> jsonAdapter = moshi.adapter(GenericEvent.class);
                @SuppressWarnings("ConstantConditions")
                InputStream in = response.body().byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                // Read line by line (append file)
                while ((line = reader.readLine()) != null) {
                    // Create an event per line
                    GenericEvent event = jsonAdapter.fromJson(line);
                    events.add(event);
                }
            }
        });
    }
    
    private void setupFields() {
        okHttpClient = new OkHttpClient();
        prefManager = new PrefManager(this);
        fragmentManager = getSupportFragmentManager();
        firebaseAnalytics = Serval.getFirebaseAnalytics(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setupLocationUpdate();
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
                    Location lastLocation = locationResult.getLastLocation();
                    
                    if (LocationUtil.isBetterLocation(lastLocation, MainActivity.lastLocation)) {
                        MainActivity.lastLocation = locationResult.getLastLocation();
                    }
                }
            };
        }
    }
    
    private void loadView(Bundle savedInstanceState) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        if (savedInstanceState == null) {
            transaction.replace(R.id.content, new DashboardFragment());
        } else {
            switch (prefManager.getBottomNavigationSelectedItemId()) {
                case R.id.action_dashboard:
                    transaction.replace(R.id.content, new DashboardFragment());
                    break;
                case R.id.action_events:
                    transaction.replace(R.id.content, new EventsFragment());
                    break;
                case R.id.action_map:
                    transaction.replace(R.id.content, new MapFragment());
                    break;
                default:
                    transaction.replace(R.id.content, new PlaceholderFragment());
            }
        }
        transaction.commit();
    }
    
    private void checkForNewVersion() {
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int lastKnownVersionCode = prefManager.getLastKnownVersionCode();
            
            if (prefManager.showChangelog() && lastKnownVersionCode < versionCode) {
                showChangelog(versionCode);
                prefManager.setLastKnownVersionCode(versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    private void showChangelog(int versionCode) {
        String versionName = String.format(
                Locale.getDefault(),
                getString(R.string.changelog),
                getString(R.string.versionName)
        );
        String changelogFile = String.format(getString(R.string.file_changelog), versionCode);
        
        if (prefManager.useBottomSheetDialogs()) {
            showChangelogBottomSheetDialog(versionName, changelogFile);
        } else {
            showChangelogDialog(versionName, changelogFile);
        }
    }
    
    private void showChangelogBottomSheetDialog(String versionName, String changelogFile) {
        View changelogView = View.inflate(this, R.layout.changelog_bottom_sheet_dialog, null);
        TextView versionInfo = changelogView.findViewById(R.id.version_info);
        versionInfo.setText(versionName);
        MarkdownView changelog = changelogView.findViewById(R.id.changelog);
        changelog.loadMarkdownFile(changelogFile);
        BottomSheetDialog changelogDialog = new BottomSheetDialog(this);
        changelogDialog.setContentView(changelogView);
        changelogDialog.show();
    }
    
    private void showChangelogDialog(String versionName, String changelogFile) {
        MaterialDialog changelogDialog =
                new MaterialDialog.Builder(this).title(versionName)
                                                .customView(R.layout.changelog_dialog, true)
                                                .positiveText(R.string.ok)
                                                .show();
        View view = changelogDialog.getCustomView();
        MarkdownView changelog = view.findViewById(R.id.changelog);
        changelog.loadMarkdownFile(changelogFile);
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
}
