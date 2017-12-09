package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import ca.allanwang.kau.utils.startActivity
import de.uni_marburg.mathematik.ds.serval.utils.Prefs

class StartActivity : KauBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Prefs.isFirstLaunch) startActivity(IntroActivity::class.java)
        else startActivity(MainActivity::class.java)
    }
}