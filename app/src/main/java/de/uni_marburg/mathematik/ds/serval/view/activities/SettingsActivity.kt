package de.uni_marburg.mathematik.ds.serval.view.activities


import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import ca.allanwang.kau.utils.string
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
        with(supportActionBar) {
            title = string(R.string.settings)
            this?.setDisplayHomeAsUpEnabled(true)
        }
        Aardvark.firebaseAnalytics.setCurrentScreen(this, string(R.string.screen_settings), null)
        supportFragmentManager.beginTransaction().replace(R.id.content, SettingsFragment()).commit()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
