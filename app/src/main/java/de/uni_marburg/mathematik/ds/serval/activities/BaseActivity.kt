package de.uni_marburg.mathematik.ds.serval.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import de.uni_marburg.mathematik.ds.serval.utils.setAardvarkTheme
import de.uni_marburg.mathematik.ds.serval.utils.setCurrentScreen
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag

/** Created by thames1990 on 04.12.17. */
abstract class BaseActivity : KauBaseActivity() {

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