package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.controller.IntroAdapter;
import de.uni_marburg.mathematik.ds.serval.view.util.IntroPageTransformer;

/**
 * Alternative intro sliders view.
 */
public class IntroActivity extends AppCompatActivity {

    @BindView(R.id.viewpager)
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro_layout);
        ButterKnife.bind(this);
        viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager()));
        viewPager.setPageTransformer(false, new IntroPageTransformer());
    }
}
