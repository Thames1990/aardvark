package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.uni_marburg.mathematik.ds.serval.Aardvark

abstract class BaseFragment : Fragment() {

    protected abstract val layout: Int

    override fun onCreateView(
            inflater: LayoutInflater?,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater?.inflate(layout, container, false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Aardvark.firebaseAnalytics.setCurrentScreen(activity, this::class.java.simpleName, null)
    }

    override fun onDestroy() {
        super.onDestroy()
        Aardvark.refWatcher.watch(this)
    }
}
