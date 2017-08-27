package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;

import static android.os.Build.VERSION;
import static android.os.Build.VERSION_CODES;

public class WelcomeActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    @BindView(R.id.layoutDots)
    LinearLayout dotsLayout;
    @BindView(R.id.btn_next)
    Button btnNext;
    @BindView(R.id.btn_skip)
    Button btnSkip;
    @BindView(R.id.view_pager)
    ViewPager viewPager;

    private int[] layouts;
    private PrefManager prefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Checking if it's the first launch
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }

        // Making notification bar transparent
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            );
        }

        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);

        btnSkip = findViewById(R.id.btn_skip);
        btnNext = findViewById(R.id.btn_next);

        // Layouts of all welcome slider
        layouts = new int[]{
                R.layout.welcome_slide1,
                R.layout.welcome_slide2,
                R.layout.welcome_slide3,
                R.layout.welcome_slide4,
        };

        addBottomDots(0);
        changeStatusBarColor();

        viewPager.setAdapter(new WelcomeViewPagerAdapter());
        viewPager.addOnPageChangeListener(this);

        btnSkip.setOnClickListener(view -> launchHomeScreen());
        btnNext.setOnClickListener(view -> {
            // Checking for last page
            int current = getItem(+1);
            if (current < layouts.length) {
                // Move to next screen
                viewPager.setCurrentItem(current);
            } else {
                launchHomeScreen();
            }
        });
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];

        int[] colorsActive = getResources().getIntArray(R.array.array_dot_active);
        int[] colorsInactive = getResources().getIntArray(R.array.array_dot_inactive);

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            if (VERSION.SDK_INT >= VERSION_CODES.N) {
                dots[i].setText(Html.fromHtml("&#8226;", Html.FROM_HTML_MODE_LEGACY));
            } else {
                dots[i].setText(Html.fromHtml("&#8226;"));
            }
            dots[i].setTextSize(35);
            dots[i].setTextColor(colorsInactive[currentPage]);
            dotsLayout.addView(dots[i]);
        }

        if (dots.length > 0) {
            dots[currentPage].setTextColor(colorsActive[currentPage]);
        }
    }

    private int getItem(int offset) {
        return viewPager.getCurrentItem() + offset;
    }

    private void launchHomeScreen() {
        prefManager.setIsFirstTimeLaunch(false);
        startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        finish();
    }

    @Override
    public void onPageSelected(int position) {
        addBottomDots(position);

        // Changing the next button text
        if (position == layouts.length - 1) {
            // Last page
            btnNext.setText(getString(R.string.done));
            btnSkip.setVisibility(View.GONE);
        } else if (position == 2) {
            checkLocationPermission();
        } else {
            // Pages are still left
            btnNext.setText(getString(R.string.next));
            btnSkip.setVisibility(View.VISIBLE);
        }
    }

    /**
     * TODO React to permission denial
     */
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
        ) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, MainActivity.CHECK_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /**
     * Making notification bar transparent
     */
    private void changeStatusBarColor() {
        if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
    }

    private class WelcomeViewPagerAdapter extends PagerAdapter {

        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }

}
