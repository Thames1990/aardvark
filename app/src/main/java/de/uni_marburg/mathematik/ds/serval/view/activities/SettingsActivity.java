package de.uni_marburg.mathematik.ds.serval.view.activities;


import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.Aardvark;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.view.fragments.SettingsFragment;

/**
 * Settings view
 */
public class SettingsActivity extends AppCompatActivity {
    
    @SuppressWarnings({"WeakerAccess", "CanBeFinal"})
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        setupActionBar();
        getSupportFragmentManager().beginTransaction()
                                   .replace(R.id.content, new SettingsFragment())
                                   .commit();
        Aardvark.getFirebaseAnalytics(this)
                .setCurrentScreen(this, getString(R.string.screen_settings), null);
    }
    
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle(getString(R.string.settings));
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
