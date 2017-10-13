package de.uni_marburg.mathematik.ds.serval.view.activities

import android.content.Intent
import android.support.v4.app.TaskStackBuilder
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.util.Preferences

class SplashActivity : AppCompatActivity() {

    override fun onStart() {
        super.onStart()
        if (Preferences.isFirstLaunch) {
            TaskStackBuilder
                    .create(this)
                    .addNextIntentWithParentStack(Intent(this, MainActivity::class.java))
                    .addNextIntent(Intent(this, IntroActivity::class.java))
                    .startActivities()
        } else startActivity(MainActivity::class.java, true)
    }
}