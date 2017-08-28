package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.BottomSheetDialog;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.BuildConfig;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.GenericEventAdapter;
import de.uni_marburg.mathematik.ds.serval.model.GenericEvent;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import us.feras.mdv.MarkdownView;

import static android.support.v7.widget.DividerItemDecoration.VERTICAL;

/**
 * Main view of the app.
 * <p>
 * Currently shows a list of all events. Might be changed to a dashboard.
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Key for the request code to permit location checks
     */
    public static final int CHECK_LOCATION_PERMISSION = 0;

    /**
     * List of events
     */
    private List<GenericEvent> events;

    private GenericEventAdapter adapter;

    private PrefManager prefManager;

    @BindView(R.id.appbar)
    AppBarLayout appBarLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefManager = new PrefManager(this);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        initRecyclerView();
        loadData();
        checkForNewVersion();
    }

    private void initRecyclerView() {
//        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        events = new ArrayList<>();
        adapter = new GenericEventAdapter(events);
        recyclerView.setAdapter(adapter);
    }

    private void loadData() {
        Request request = new Request.Builder()
                .url(getString(R.string.url_rest_api))
                .build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response)
                    throws IOException {
                if (!response.isSuccessful()) {
                    throw new IOException();
                }

                Gson gson = new Gson();
                InputStream in = response.body().byteStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                // Read line by line (append file)
                while ((line = reader.readLine()) != null) {
                    // Create an event per line
                    GenericEvent testItem = gson.fromJson(line, GenericEvent.class);
                    events.add(testItem);
                    runOnUiThread(() -> adapter.notifyItemChanged(events.size()));
                }
            }
        });
    }

    /**
     * Checks if a new version was installed.
     */
    private void checkForNewVersion() {
        try {
            int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
            int lastKnownVersionCode = prefManager.getLastKnownVersionCode();

            if (lastKnownVersionCode < versionCode || BuildConfig.DEBUG) {
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
        String changelogFile = String.format(
                Locale.getDefault(),
                getString(R.string.changelog_file),
                versionCode
        );

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
                startActivity(new Intent(this, WelcomeActivity.class));
                ActivityCompat.finishAffinity(this);
                return true;
            case R.id.action_alternative_intro:
                startActivity(new Intent(this, IntroActivity.class));
                return true;
            case R.id.action_settings:
                startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        return Math.round(TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                getResources().getDisplayMetrics()
        ));
    }

}
