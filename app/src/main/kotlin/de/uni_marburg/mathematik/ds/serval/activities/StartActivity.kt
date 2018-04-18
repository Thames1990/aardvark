package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.settings.Prefs

/**
 * Delegation activity, that determines, if the intro should be shown, or if the main view should
 * be opened.
 */
class StartActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.lastLaunch == NOT_LAUNCHED_BEFORE) startActivity<IntroActivity>()
        else startActivity<MainActivity>()
    }

    companion object {
        const val NOT_LAUNCHED_BEFORE: Long = -1L
    }

}