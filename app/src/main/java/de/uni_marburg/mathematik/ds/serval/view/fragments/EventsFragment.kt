package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator.*
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.util.withDividerDecoration
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import kotlinx.android.synthetic.main.fragment_events.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val eventAdapter: EventAdapter by lazy {
        EventAdapter(activity!!) {
            context!!.startActivity<DetailActivity>(DetailActivity.EVENT to it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupRecyclerView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater?.inflate(R.menu.menu_events, menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        menu?.let {
            super.onPrepareOptionsMenu(menu)
            val filterEvents = menu.findItem(R.id.action_filter_events)
            val icon = context!!.drawable(R.drawable.filter_list)
            icon.setColorFilter(context!!.color(android.R.color.white), PorterDuff.Mode.SRC_IN)
            filterEvents.icon = icon
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = with(eventAdapter) {
        when (item.itemId) {
            R.id.sort_distance_ascending      -> consume { sortEventsBy(DISTANCE) }
            R.id.sort_distance_descending     -> consume { sortEventsBy(DISTANCE, true) }
            R.id.sort_measurements_ascending  -> consume { sortEventsBy(MEASUREMENT) }
            R.id.sort_measurements_descending -> consume { sortEventsBy(MEASUREMENT, true) }
            R.id.sort_time_ascending          -> consume { sortEventsBy(TIME) }
            R.id.sort_time_descending         -> consume { sortEventsBy(TIME, true) }
            else                              -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        with(context!!) {
            if (isNetworkAvailable) doAsync {
                eventAdapter.loadEvents()
                uiThread {
                    recycler_view.apply {
                        withLinearAdapter(eventAdapter)
                        withDividerDecoration(this@with, DividerItemDecoration.VERTICAL)
                        setBackgroundColor(Prefs.colorBackground)
                    }
                }
            } else toast(string(R.string.toast_network_disconnected))
        }
    }

    private fun setupRefresh() {
        with(context!!) {
            if (isNetworkAvailable) swipeRefreshLayout.apply {
                setOnRefreshListener {
                    doAsync {
                        eventAdapter.loadEvents()
                        uiThread { isRefreshing = false }
                    }
                }
            } else toast(string(R.string.toast_network_disconnected))
        }
    }
}
