package de.uni_marburg.mathematik.ds.serval.view.activities


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.view.fragments.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*

/** Settings view */
class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = getString(R.string.settings)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
                .beginTransaction()
                .replace(R.id.content, SettingsFragment())
                .commit()
        Aardvark.firebaseAnalytics.setCurrentScreen(this, getString(R.string.screen_settings), null)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }
}
