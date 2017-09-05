package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.BuildConfig;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.model.event.GenericEvent;
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

/**
 * Main view of the app.
 * <p>
 * Currently shows a list of all events. Might be changed to a dashboard.
 */
public class MainActivity
        extends AppCompatActivity
        implements BottomNavigationView.OnNavigationItemSelectedListener {
    
    private static final int NUMBER_OF_EVENTS_PASSED = 50;
    
    /**
     * List of events
     */
    private List<GenericEvent> events;
    
    private PrefManager prefManager;
    
    private FragmentManager fragmentManager;
    
    private FirebaseAnalytics firebaseAnalytics;
    
    private OkHttpClient okHttpClient;
    
    @BindView(R.id.bottom_navigation)
    BottomNavigationView bottomNavigationView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setupFields();
        setupBottomNavigationView();
        checkForNewVersion();
    }
    
    private void setupFields() {
        okHttpClient = new OkHttpClient();
        prefManager = new PrefManager(this);
        loadData();
        fragmentManager = getSupportFragmentManager();
        firebaseAnalytics = Serval.getFirebaseAnalytics(this);
    }
    
    private void loadData() {
        events = new ArrayList<>();
        Request request = new Request.Builder().url(getString(R.string.url_rest_api)).build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(getClass().getSimpleName(),
                      String.format(getString(R.string.exception_load_data_failed),
                                    e.getMessage()));
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
                BufferedReader reader =
                        new BufferedReader(new InputStreamReader(in));
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
    
    private void setupBottomNavigationView() {
        bottomNavigationView.setSelectedItemId(R.id.action_dashboard);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }
    
    /**
     * Checks if a new version was installed.
     */
    private void checkForNewVersion() {
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int lastKnownVersionCode = prefManager.getLastKnownVersionCode();
            
            if (prefManager.showChangelog() &&
                (BuildConfig.DEBUG || lastKnownVersionCode < versionCode)) {
                Log.d(getClass().getSimpleName(), "We did it boys!");
                showChangelog(versionCode);
                prefManager.setLastKnownVersionCode(versionCode);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Shows the changelog of the most recent update.
     *
     * @param versionCode Version code of the app
     */
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
    
    /**
     * Shows the changelog of the most recent update in a
     * {@link BottomSheetDialog bottom sheet dialog}.
     *
     * @param versionName   Version name of the app
     * @param changelogFile Location of the changelog file
     */
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
    
    /**
     * Shows the changelog of the most recent update in a {@link Dialog dialog}.
     *
     * @param versionName   Version name of the app
     * @param changelogFile Location of the changelog file
     */
    private void showChangelogDialog(String versionName, String changelogFile) {
        MaterialDialog changelogDialog = new MaterialDialog.Builder(this)
                .title(versionName)
                .customView(R.layout.changelog_dialog, true)
                .positiveText(R.string.ok)
                .show();
        View view = changelogDialog.getCustomView();
        if (view != null) {
            MarkdownView changelog = view.findViewById(R.id.changelog);
            changelog.loadMarkdownFile(changelogFile);
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
        new MaterialDialog.Builder(this)
                .title(R.string.confirm_exit)
                .positiveText(R.string.exit)
                .onPositive((dialog, which) -> finish())
                .negativeText(R.string.cancel)
                .show();
    }
    
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // TODO Based on distance
        Collections.sort(
                events,
                (event1, event2) -> (int) (event2.getTime() - event1.getTime())
        );
        ArrayList lastEvents = new ArrayList<>(events.subList(
                0,
                Math.min(events.size(), NUMBER_OF_EVENTS_PASSED)
        ));
        
        Fragment fragment = null;
        Fragment currentFragment = fragmentManager.findFragmentById(R.id.content);
        
        switch (item.getItemId()) {
            case R.id.action_dashboard:
                if (!(currentFragment instanceof DashboardFragment)) {
                    fragment = DashboardFragment.newInstance(lastEvents);
                    firebaseAnalytics.setCurrentScreen(MainActivity.this,
                                                       getString(R.string.screen_dashboard),
                                                       null);
                }
                break;
            case R.id.action_events:
                if (!(currentFragment instanceof EventsFragment)) {
                    fragment = EventsFragment.newInstance(lastEvents);
                    firebaseAnalytics.setCurrentScreen(MainActivity.this,
                                                       getString(R.string.screen_events),
                                                       null);
                }
                break;
            case R.id.action_map:
                if (!(currentFragment instanceof MapFragment)) {
                    fragment = MapFragment.newInstance(lastEvents);
                    firebaseAnalytics.setCurrentScreen(MainActivity.this,
                                                       getString(R.string.screen_map),
                                                       null);
                }
                break;
            default:
                fragment = PlaceholderFragment.newInstance();
        }
        
        if (fragment != null) {
            fragmentManager.beginTransaction()
                           .replace(R.id.content, fragment)
                           .commit();
            return true;
        }
        
        return false;
    }
}
