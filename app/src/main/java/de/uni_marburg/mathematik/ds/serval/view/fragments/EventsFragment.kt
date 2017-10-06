package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.content.Intent
import android.graphics.Color
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
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator
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

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_filter_events_distance_ascending ->
                eventAdapter.sort(EventComparator.Distance, location = MainActivity.lastLocation)
            R.id.action_filter_events_distance_descending ->
                eventAdapter.sort(EventComparator.Distance, true, MainActivity.lastLocation)
            R.id.action_filter_events_measurements_ascending ->
                eventAdapter.sort(EventComparator.Measurement)
            R.id.action_filter_events_measurements_descending ->
                eventAdapter.sort(EventComparator.Measurement, true)
            R.id.action_filter_events_time_ascending ->
                eventAdapter.sort(EventComparator.Time)
            R.id.action_filter_events_time_descending ->
                eventAdapter.sort(EventComparator.Time, true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter {
            val detail = Intent(activity, DetailActivity::class.java)
            detail.putExtra(DetailActivity.EVENT, it)
            startActivity(detail)
        }

        with(recycler_view) {
            layoutManager = LinearLayoutManager(context)
            addItemDecoration(DividerItemDecoration(
                    context,
                    DividerItemDecoration.VERTICAL
            ))
            adapter = eventAdapter
        }

        with(swipeRefreshLayout) {
            setColorSchemeColors(Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW)
            setOnRefreshListener {
                eventAdapter.loadEvents(true)
                eventAdapter.sort(EventComparator.Time)
                isRefreshing = false
            }
        }

        eventAdapter.loadEvents()
    }
}
