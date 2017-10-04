package de.uni_marburg.mathematik.ds.serval.view.activities

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Intent
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.v4.app.ActivityCompat.requestPermissions
import android.support.v4.view.ViewPager
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.WindowManager
import ca.allanwang.kau.utils.hasPermission
import ca.allanwang.kau.utils.string
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.adapters.IntroAdapter
import de.uni_marburg.mathematik.ds.serval.util.CHECK_LOCATION_PERMISSION
import de.uni_marburg.mathematik.ds.serval.util.PERMISSION_TAB
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.util.IntroPageTransformer
import kotlinx.android.synthetic.main.intro_layout.*

/** Alternative intro sliders view. */
class IntroActivity : AppCompatActivity() {

    private var pageScrollCounter = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (!Preferences.isFirstLaunch) {
            launchHomeScreen()
        }

        Aardvark.firebaseAnalytics.setCurrentScreen(this, string(R.string.screen_intro), null)

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
        content.addOnPageChangeListener(
                object : ViewPager.OnPageChangeListener {
                    override fun onPageScrollStateChanged(state: Int) {

                    }

                    override fun onPageScrolled(
                            position: Int,
                            positionOffset: Float,
                            positionOffsetPixels: Int
                    ) {
                        if (position == content.adapter.count - 1 && positionOffset == 0f) {
                            if (pageScrollCounter != 0) {
                                launchHomeScreen()
                            }
                            pageScrollCounter++
                        } else {
                            pageScrollCounter = 0
                        }
                    }

                    override fun onPageSelected(position: Int) {
                        when (position) {
                            PERMISSION_TAB -> checkLocationPermission()
                        }
                    }
                })
    }

    private fun launchHomeScreen() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun checkLocationPermission() {
        if (!hasPermission(ACCESS_FINE_LOCATION)) {
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
                Preferences.trackLocation = grantResults[0] == PERMISSION_GRANTED
            else ->
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
}
