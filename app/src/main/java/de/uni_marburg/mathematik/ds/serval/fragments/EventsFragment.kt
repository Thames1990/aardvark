package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.Aardvark
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.controller.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator.*
import de.uni_marburg.mathematik.ds.serval.model.event.EventRepository
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.withDividerDecoration
import kotlinx.android.synthetic.main.fragment_events.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread

class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private lateinit var eventAdapter: EventAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean = with(eventAdapter) {
        when (item.itemId) {
            R.id.sort_distance_ascending -> sortEventsBy(DISTANCE)
            R.id.sort_distance_descending -> sortEventsBy(DISTANCE, true)
            R.id.sort_measurements_ascending -> sortEventsBy(MEASUREMENT)
            R.id.sort_measurements_descending -> sortEventsBy(MEASUREMENT, true)
            R.id.sort_time_ascending -> sortEventsBy(TIME)
            R.id.sort_time_descending -> sortEventsBy(TIME, true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupRecyclerView() {
        eventAdapter = EventAdapter {
            context!!.startActivity<DetailActivity>(
                    DetailActivity.EVENT to it, DetailActivity.SHOW_MAP to true
            )
        }

        recycler_view.apply {
            withLinearAdapter(eventAdapter)
            withDividerDecoration(context, DividerItemDecoration.VERTICAL)
            setBackgroundColor(Prefs.backgroundColor)
        }

        doAsync {
            val events: List<Event>

            if (context!!.isNetworkAvailable) {
                events = EventRepository.fetch()
                Aardvark.eventDao.insertAll(events)
            } else {
                events = Aardvark.eventDao.getAll()
            }

            uiThread {
                eventAdapter.events = events
            }
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
                        val events: List<Event> = EventRepository.fetch()
                        eventAdapter.events = events
                        Aardvark.eventDao.insertAll(events)
                        uiThread { isRefreshing = false }
                    }
                }
            } else toast(string(R.string.toast_network_disconnected))
        }
    }
}
