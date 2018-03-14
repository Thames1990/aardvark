package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import ca.allanwang.kau.internal.KauBaseActivity
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.getViewModel
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag
import de.uni_marburg.mathematik.ds.serval.utils.setTheme

/**
 * Base activity for any activity that would have extended [AppCompatActivity].
 *
 * Ensures that some singleton methods are called.
 * This is simply a convenience class.
 */
abstract class BaseActivity : KauBaseActivity() {

    /**
     * Defines consuming back button presses.
     *
     * If this evaluates to *true*, no further action will be taken, once the back button is
     * pressed. Otherwise the default Android implementation is executed.
     */
    protected open fun backConsumer(): Boolean = false

    protected lateinit var viewModel: EventViewModel

    override fun onBackPressed() {
        if (backConsumer()) return
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = getViewModel(activity = this)

        setSecureFlag()
        setTheme()
    }

}