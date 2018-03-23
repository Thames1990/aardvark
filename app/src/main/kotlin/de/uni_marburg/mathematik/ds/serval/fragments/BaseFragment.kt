package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ca.allanwang.kau.utils.Kotterknife
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.getViewModel

abstract class BaseFragment : Fragment() {

    protected abstract val layout: Int

    protected lateinit var eventViewModel: EventViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel = getViewModel()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(layout, container, false)

    override fun onDestroy() {
        super.onDestroy()
        Aardvark.refWatcher.watch(this)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        Kotterknife.reset(this)
    }
}
