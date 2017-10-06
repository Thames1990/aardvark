package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.content.Intent
import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator.*
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.view.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_events.*

class EventsFragment : BaseFragment() {

    private lateinit var eventAdapter: EventAdapter

    override val layout: Int
        get() = R.layout.fragment_events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Aardvark.firebaseAnalytics.setCurrentScreen(activity, getString(R.string.screen_events), null)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
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

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        R.id.action_filter_events_distance_ascending ->
            consume { eventAdapter.sortBy(Distance, location = MainActivity.lastLocation) }
        R.id.action_filter_events_distance_descending ->
            consume { eventAdapter.sortBy(Distance, true, MainActivity.lastLocation) }
        R.id.action_filter_events_measurements_ascending ->
            consume { eventAdapter.sortBy(Measurement) }
        R.id.action_filter_events_measurements_descending ->
            consume { eventAdapter.sortBy(Measurement, true) }
        R.id.action_filter_events_time_ascending -> consume { eventAdapter.sortBy(Time) }
        R.id.action_filter_events_time_descending -> consume { eventAdapter.sortBy(Time, true) }
        else -> super.onOptionsItemSelected(item)
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter {
            val detail = Intent(activity, DetailActivity::class.java)
            detail.putExtra(DetailActivity.EVENT, it)
            startActivity(detail)
        }
        eventAdapter.loadEvents(true)

        with(recycler_view) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
            ))
            adapter = eventAdapter
        }

        with(swipeRefreshLayout) {
            setOnRefreshListener {
                eventAdapter.loadEvents(true)
                eventAdapter.sortBy(Time)
                isRefreshing = false
            }
        }
    }
}
