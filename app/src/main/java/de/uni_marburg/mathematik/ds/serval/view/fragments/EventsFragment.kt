package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.utils.isNetworkAvailable
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.toast
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator.*
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import kotlinx.android.synthetic.main.fragment_events.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val eventAdapter: EventAdapter by lazy {
        EventAdapter(activity) {
            context.startActivity<DetailActivity>(DetailActivity.EVENT to it)
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
            val icon = ContextCompat.getDrawable(context, R.drawable.filter_list)
            icon.setColorFilter(
                    ContextCompat.getColor(context, android.R.color.white),
                    PorterDuff.Mode.SRC_IN
            )
            filterEvents.icon = icon
        }
    }

    override fun onOptionsItemSelected(item: MenuItem) = with(eventAdapter) {
        when (item.itemId) {
            R.id.sort_distance_ascending -> consume { sortBy(Distance) }
            R.id.sort_distance_descending -> consume { sortBy(Distance, true) }
            R.id.sort_measurements_ascending -> consume { sortBy(Measurement) }
            R.id.sort_measurements_descending -> consume { sortBy(Measurement, true) }
            R.id.sort_time_ascending -> consume { sortBy(Time) }
            R.id.sort_time_descending -> consume { sortBy(Time, true) }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupRecyclerView() {
        doAsync {
            with(context) {
                if (isNetworkAvailable) eventAdapter.loadEvents()
                uiThread {
                    if (!isNetworkAvailable) toast(string(R.string.toast_network_disconnected))
                    with(recycler_view) {
                        layoutManager = LinearLayoutManager(context)
                        addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
                        adapter = eventAdapter
                    }
                }
            }
        }
    }

    private fun setupRefresh() = with(swipeRefreshLayout) {
        setOnRefreshListener {
            doAsync {
                with(context) {
                    if (isNetworkAvailable) eventAdapter.loadEvents()
                    uiThread {
                        if (!isNetworkAvailable) toast(string(R.string.toast_network_disconnected))
                        isRefreshing = false
                    }
                }
            }
        }
    }
}
