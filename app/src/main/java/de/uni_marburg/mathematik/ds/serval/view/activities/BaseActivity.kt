package de.uni_marburg.mathematik.ds.serval.view.activities

import android.os.Bundle
import ca.allanwang.kau.internal.KauBaseActivity
import de.uni_marburg.mathematik.ds.serval.util.setAardvarkTheme

/** Created by thames1990 on 04.12.17. */
abstract class BaseActivity : KauBaseActivity() {

    protected open fun backConsumer(): Boolean = false

    override fun onBackPressed() {
        if (backConsumer()) return
        super.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setAardvarkTheme()
    }
}