package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.DetailAdapter;
import de.uni_marburg.mathematik.ds.serval.model.TestItem;

public class DetailActivity extends AppCompatActivity {

    public static final String ITEM = "ITEM";

    private TestItem item;

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
        item = (TestItem) getIntent().getSerializableExtra(ITEM);
        setupViews();
    }

    private void setupViews() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewPager.setAdapter(new DetailAdapter(getSupportFragmentManager(), item, this));
        tabLayout.setupWithViewPager(viewPager);
        fab.setOnClickListener(view -> Snackbar.make(
                tabLayout,
                getString(R.string.coming_soon),
                Snackbar.LENGTH_SHORT
        ).show());
    }

}
