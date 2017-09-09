package de.uni_marburg.mathematik.ds.serval.view.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.uni_marburg.mathematik.ds.serval.R;
import de.uni_marburg.mathematik.ds.serval.Serval;
import de.uni_marburg.mathematik.ds.serval.controller.adapters.IntroAdapter;
import de.uni_marburg.mathematik.ds.serval.util.PrefManager;
import de.uni_marburg.mathematik.ds.serval.view.page_transformers.IntroPageTransformer;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * Alternative intro sliders view.
 */
public class IntroActivity
        extends AppCompatActivity
        implements ViewPager.OnPageChangeListener, Animation.AnimationListener {
    
    private static final int CHECK_LOCATION_PERMISSION = 0;
    
    private static final int FADE_OUT_ANIMATION_DURATION = 1000;
    
    private static final int PERMISSION_TAB = 2;
    
    private PrefManager prefManager;
    
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Checking if it's the first launch
        prefManager = new PrefManager(this);
        if (!prefManager.isFirstTimeLaunch()) {
            launchHomeScreen();
            finish();
        }
        
        Serval.getFirebaseAnalytics(this)
              .setCurrentScreen(this, getString(R.string.screen_intro), null);
        
        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.getDecorView()
                  .setSystemUiVisibility(
                          View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                  );
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }
        
        setContentView(R.layout.intro_layout);
        ButterKnife.bind(this);
        
        viewPager.setAdapter(new IntroAdapter(getSupportFragmentManager(), this));
        viewPager.setPageTransformer(false, new IntroPageTransformer(this));
        viewPager.addOnPageChangeListener(this);
    }
    
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        
    }
    
    @Override
    public void onPageSelected(int position) {
        if (position == PERMISSION_TAB) {
            checkLocationPermission();
        }
    }
    
    @Override
    public void onPageScrollStateChanged(int state) {
        
    }
    
    private void launchHomeScreen() {
        prefManager.setIsFirstTimeLaunch(false);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
    
    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{ACCESS_FINE_LOCATION},
                    CHECK_LOCATION_PERMISSION
            );
        }
    }
    
    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            @NonNull String[] permissions,
            @NonNull int[] grantResults
    ) {
        switch (requestCode) {
            case CHECK_LOCATION_PERMISSION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    prefManager.setRequestLocationUpdates(true);
                } else {
                    prefManager.setRequestLocationUpdates(false);
                }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
    
    public void startApp(View view) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setFillAfter(true);
        fadeOut.setDuration(FADE_OUT_ANIMATION_DURATION);
        fadeOut.setAnimationListener(this);
        view.startAnimation(fadeOut);
    }
    
    @Override
    public void onAnimationStart(Animation animation) {
        
    }
    
    @Override
    public void onAnimationEnd(Animation animation) {
        launchHomeScreen();
    }
    
    @Override
    public void onAnimationRepeat(Animation animation) {
        
    }
}
