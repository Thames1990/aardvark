package de.uni_marburg.mathematik.ds.serval.view.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.util.Prefs

class StartActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.lastLaunch != -1L) startActivity(MainActivity::class.java)
        else startActivity(IntroActivity2::class.java)
    }
}