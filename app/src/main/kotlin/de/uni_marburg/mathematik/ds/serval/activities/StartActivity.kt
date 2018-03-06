package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.currentTimeInMillis

/**
 * Delegation activity, that determines, if the intro should be shown, or if the main view should
 * be openend.
 */
class StartActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.lastLaunch == -1L) startActivity<IntroActivity>()
        else startActivity<MainActivity>()
        Prefs.lastLaunch = currentTimeInMillis
    }
}