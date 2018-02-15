package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import de.uni_marburg.mathematik.ds.serval.utils.setAardvarkTheme
import de.uni_marburg.mathematik.ds.serval.utils.setCurrentScreen
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag

abstract class BaseActivity : KauBaseActivity() {

    /**
     * Defines consuming back button presses.
     *
     * If this evaluates to *true*, no further action will be taken, once the back button is
     * pressed. Otherwise the default Android implementation is executed.
     */
    protected open fun backConsumer(): Boolean = false

    override fun onBackPressed() {
        if (backConsumer()) return
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setCurrentScreen()
        setSecureFlag()
        setAardvarkTheme()
    }
}