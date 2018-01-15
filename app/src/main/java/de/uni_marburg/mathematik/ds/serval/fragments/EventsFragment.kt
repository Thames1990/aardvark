package de.uni_marburg.mathematik.ds.serval.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.Menu
import android.view.MenuInflater
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.model.event.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.withDividerDecoration
import kotlinx.android.synthetic.main.fragment_events.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val eventAdapter = EventAdapter {
        context!!.startActivity<DetailActivity>(
                DetailActivity.EVENT to it,
                DetailActivity.SHOW_MAP to true
        )
    }

    private val eventViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        eventViewModel.allEvents.observe(this, Observer(eventAdapter::setList))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
        setupRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_events, menu)
        activity?.setMenuIcons(
                menu = menu,
                color = Prefs.iconColor,
                iicons = *arrayOf(R.id.action_filter_events to GoogleMaterial.Icon.gmd_filter_list)
        )
    }

    private fun setupRecyclerView() {
        recycler_view.apply {
            withLinearAdapter(eventAdapter)
            withDividerDecoration(context, DividerItemDecoration.VERTICAL)
            setBackgroundColor(Prefs.backgroundColor)
        }

        if (context!!.isNetworkAvailable) {
            eventViewModel.reload()
        }
    }

    private fun setupRefresh() {
        with(context!!) {
            if (isNetworkAvailable) swipeRefreshLayout.apply {
                setOnRefreshListener {
                    setColorSchemeColors(
                            Prefs.backgroundColor,
                            Prefs.accentColor,
                            Prefs.backgroundColor
                    )
                    doAsync {
                        eventViewModel.reload()
                        uiThread { isRefreshing = false }
                    }
                }
            } else toast(string(R.string.toast_network_disconnected))
        }
    }
}
