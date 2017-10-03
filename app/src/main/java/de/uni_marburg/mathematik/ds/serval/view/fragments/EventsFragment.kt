package de.uni_marburg.mathematik.ds.serval.view.fragments

import android.graphics.PorterDuff
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.controller.adapters.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.EventComparator
import de.uni_marburg.mathematik.ds.serval.util.ImageUtil
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import de.uni_marburg.mathematik.ds.serval.view.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.view.util.GridSpacingItemDecoration
import kotlinx.android.synthetic.main.fragment_events.*

class EventsFragment : BaseFragment() {

    private lateinit var adapter: EventAdapter

    override val layout: Int
        get() = R.layout.fragment_events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
    }

    override fun onPrepareOptionsMenu(menu: Menu?) {
        super.onPrepareOptionsMenu(menu)
        val filterEvents = menu!!.findItem(R.id.action_filter_events)
        val icon = ContextCompat.getDrawable(context, R.drawable.filter_list)
        icon.setColorFilter(
                ContextCompat.getColor(context, android.R.color.white),
                PorterDuff.Mode.SRC_IN
        )
        filterEvents.icon = icon
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater!!.inflate(R.menu.menu_events, menu)
        // Hide location sort, if the user denied location update permission
        if (!Preferences.trackLocation) {
            menu!!.findItem(R.id.action_filter_events_distance).isVisible = false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            R.id.action_filter_events_distance_ascending -> adapter.sort(EventComparator.Distance, location = MainActivity.lastLocation)
            R.id.action_filter_events_distance_descending -> adapter.sort(EventComparator.Distance, true, MainActivity.lastLocation)
            R.id.action_filter_events_measurements_ascending -> adapter.sort(EventComparator.Measurement)
            R.id.action_filter_events_measurements_descending -> adapter.sort(EventComparator.Measurement, true)
            R.id.action_filter_events_time_ascending -> adapter.sort(EventComparator.Time)
            R.id.action_filter_events_time_descending -> adapter.sort(EventComparator.Time, true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupRecyclerView() {
        setupLayoutManager()
        adapter = EventAdapter(MainActivity.events)
        recycler_view.adapter = adapter
    }

    private fun setupLayoutManager() {
        when {
            Preferences.useLinearLayoutManager -> {
                recycler_view.layoutManager = LinearLayoutManager(context)
                recycler_view.addItemDecoration(DividerItemDecoration(
                        context,
                        DividerItemDecoration.VERTICAL
                ))
            }
            Preferences.useGridLayoutManager -> {
                recycler_view.layoutManager = GridLayoutManager(
                        context,
                        Preferences.gridLayoutManagerSpanCount
                )
                recycler_view.addItemDecoration(GridSpacingItemDecoration(
                        Preferences.gridLayoutManagerSpanCount,
                        ImageUtil.dpToPixels(context, 10),
                        true
                ))
            }
            Preferences.useStaggeredGridLayoutManager -> {
                recycler_view.layoutManager = StaggeredGridLayoutManager(
                        Preferences.staggeredGridLayoutManagerSpanCount,
                        StaggeredGridLayoutManager.VERTICAL
                )
                // TODO Add item decoration
            }
        }
    }
}
