package de.uni_marburg.mathematik.ds.serval.view.activities;


import android.os.Bundle;
import android.view.MenuItem;

import de.uni_marburg.mathematik.ds.serval.view.fragments.MainPreferenceFragment;

/**
 * Settings view
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        getFragmentManager().beginTransaction()
                            .replace(android.R.id.content, new MainPreferenceFragment())
                            .commit();
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
