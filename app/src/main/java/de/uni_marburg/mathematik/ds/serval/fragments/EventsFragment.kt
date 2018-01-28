package de.uni_marburg.mathematik.ds.serval.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedListAdapter
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.recyclerview.extensions.DiffCallback
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventComparator
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*


class EventsFragment : BaseFragment() {

    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventViewModel: EventViewModel

    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val swipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_refresh)

    override val layout: Int
        get() = R.layout.fragment_events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventAdapter = EventAdapter { event ->
            context?.let { context ->
                context.startActivity<DetailActivity>(
                    bundleBuilder = { if (Prefs.animate) withSceneTransitionAnimation(context) },
                    intentBuilder = {
                        putExtra(DetailActivity.EVENT_ID, event.id)
                        putExtra(DetailActivity.SHOW_MAP, true)
                    }
                )
            }
        }
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.events.observe(this, Observer(eventAdapter::setList))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
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

        menu.findItem(R.id.action_filter_events_distance).isVisible =
                context!!.hasLocationPermission
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.sort_distance_ascending -> eventViewModel.sort(EventComparator.DISTANCE)
            R.id.sort_distance_descending -> eventViewModel.sort(
                eventComparator = EventComparator.DISTANCE,
                reversed = true
            )
            R.id.sort_measurements_ascending -> eventViewModel.sort(EventComparator.MEASUREMENTS)
            R.id.sort_measurements_descending -> eventViewModel.sort(
                eventComparator = EventComparator.MEASUREMENTS,
                reversed = true
            )
            R.id.sort_time_ascending -> eventViewModel.sort(EventComparator.TIME)
            R.id.sort_time_descending -> eventViewModel.sort(
                eventComparator = EventComparator.TIME,
                reversed = true
            )
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    private fun setupRecyclerView() {
        recyclerView.apply {
            itemAnimator = KauAnimator()
            withLinearAdapter(eventAdapter)
            withDividerDecoration(context, DividerItemDecoration.VERTICAL)
            setBackgroundColor(Prefs.backgroundColor)
        }
    }

    private fun setupRefresh() {
        swipeRefreshLayout.apply {
            setOnRefreshListener {
                if (context.isNetworkAvailable) {
                    doAsync {
                        eventViewModel.reload()
                        uiThread { isRefreshing = false }
                    }
                } else {
                    isRefreshing = false
                    aardvarkSnackbar(context.string(R.string.network_disconnected))
                }
            }
        }
    }
}

class EventAdapter(
    private val listener: (Event) -> Unit
) : PagedListAdapter<Event, EventHolder>(diffCallback) {

    override fun onBindViewHolder(holder: EventHolder, position: Int) =
        holder.bindTo(getItem(position), listener)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventHolder =
        EventHolder(parent)

    companion object {
        private val diffCallback = object : DiffCallback<Event>() {
            override fun areContentsTheSame(oldEvent: Event, newEvent: Event): Boolean =
                oldEvent == newEvent

            override fun areItemsTheSame(oldEvent: Event, newEvent: Event): Boolean =
                oldEvent.id == newEvent.id

        }
    }
}

class EventHolder(parent: ViewGroup) : RecyclerView.ViewHolder(
    LayoutInflater.from(parent.context).inflate(R.layout.event_row, parent, false)
) {

    private val timeView = itemView.findViewById<TextView>(R.id.time)
    private val measurementsView = itemView.findViewById<LinearLayout>(R.id.measurement_types)
    private val locationIconView = itemView.findViewById<ImageView>(R.id.location_icon)
    private val guideline = itemView.findViewById<Guideline>(R.id.guideline)
    private val locationView = itemView.findViewById<TextView>(R.id.location)

    private var event: Event? = null

    fun bindTo(event: Event?, listener: (Event) -> Unit) {
        if (event != null) {
            this.event = event.apply {
                displayTime()
                displayLocation()
                displayMeasurementTypes()
            }

            itemView.setBackgroundColor(Prefs.backgroundColor)
            itemView.setOnClickListener { listener(event) }
        }
    }

    private fun Event.displayTime() {
        val timeDifference = Calendar.getInstance().timeInMillis - time
        timeView.apply {
            text = timeDifference.timeToString(itemView.context)
            setTextColor(Prefs.textColor)
        }
    }

    private fun Event.displayLocation() {
        val context = itemView.context

        if (context.hasLocationPermission) {
            locationIconView.setIcon(
                icon = GoogleMaterial.Icon.gmd_location_on,
                color = Prefs.textColor
            )
            locationView.apply {
                text = location.distanceTo(MainActivity.lastLocation).distanceToString(context)
                setTextColor(Prefs.textColor)
            }
        } else {
            locationIconView.gone()
            locationView.gone()
            val params = guideline.layoutParams as ConstraintLayout.LayoutParams
            params.guideEnd = 0
            guideline.layoutParams = params
        }
    }

    private fun Event.displayMeasurementTypes() {
        measurementsView.apply {
            removeAllViews()
            measurements.toHashSet().forEach { measurement ->
                val icon = ImageView(itemView.context).apply {
                    setIcon(icon = measurement.type.iicon, color = Prefs.textColor)
                }
                addView(icon)
            }
        }
    }

}