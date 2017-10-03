package de.uni_marburg.mathematik.ds.serval.view.activities

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat.checkSelfPermission
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.adapters.IntroAdapter
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.util.IntroPageTransformer
import kotlinx.android.synthetic.main.intro_layout.*

/** Alternative intro sliders view. */
class IntroActivity : AppCompatActivity(),
        ViewPager.OnPageChangeListener,
        Animation.AnimationListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Preferences.isFirstLaunch) {
            launchHomeScreen()
            finish()
        }

        Aardvark.firebaseAnalytics.setCurrentScreen(this, getString(R.string.screen_intro), null)

        // Making notification bar transparent
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.decorView.systemUiVisibility =
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.TRANSPARENT
        }

        setContentView(R.layout.intro_layout)
        content.adapter = IntroAdapter(supportFragmentManager, this)
        content.setPageTransformer(false, IntroPageTransformer(this))
        content.addOnPageChangeListener(this)
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {

    }

    override fun onPageSelected(position: Int) {
        if (position == PERMISSION_TAB) {
            checkLocationPermission()
        }
    }

    override fun onPageScrollStateChanged(state: Int) {

    }

    private fun launchHomeScreen() {
        Preferences.isFirstLaunch = false
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun checkLocationPermission() {
        if (checkSelfPermission(this, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED) {
            requestPermissions(this, arrayOf(ACCESS_FINE_LOCATION), CHECK_LOCATION_PERMISSION)
        }
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<String>,
            grantResults: IntArray
    ) {
        when (requestCode) {
            CHECK_LOCATION_PERMISSION ->
                Preferences.isFirstLaunch = grantResults[0] == PERMISSION_GRANTED
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    fun startApp(view: View) {
        val fadeOut = AlphaAnimation(1f, 0f)
        fadeOut.fillAfter = true
        fadeOut.duration = FADE_OUT_ANIMATION_DURATION.toLong()
        fadeOut.setAnimationListener(this)
        view.startAnimation(fadeOut)
    }

    override fun onAnimationStart(animation: Animation) {

    }

    override fun onAnimationEnd(animation: Animation) {
        launchHomeScreen()
    }

    override fun onAnimationRepeat(animation: Animation) {

    }

    companion object {

        const val CHECK_LOCATION_PERMISSION = 0

        const val FADE_OUT_ANIMATION_DURATION = 1000

        const val PERMISSION_TAB = 2
    }
}
