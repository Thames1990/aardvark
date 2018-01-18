package de.uni_marburg.mathematik.ds.serval.fragments

import android.Manifest
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedListAdapter
import android.graphics.PorterDuff
import android.location.Location
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
import de.uni_marburg.mathematik.ds.serval.BuildConfig
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.model.location.LocationViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread
import java.util.*


class EventsFragment : BaseFragment() {

    private lateinit var eventAdapter: EventAdapter
    private lateinit var eventViewModel: EventViewModel
    private lateinit var locationViewModel: LocationViewModel

    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val swipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_refresh)

    override val layout: Int
        get() = R.layout.fragment_events

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventAdapter = EventAdapter { event ->
            context!!.startActivity<DetailActivity>(
                DetailActivity.EVENT_ID to event.id,
                DetailActivity.SHOW_MAP to true
            )
        }
        eventViewModel = ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
        eventViewModel.events.observe(this, Observer(eventAdapter::setList))
        locationViewModel = ViewModelProviders.of(activity!!).get(LocationViewModel::class.java)
        locationViewModel.locationLiveData.observe(this, Observer { location ->
            if (location != null) {
                eventAdapter.lastLocation.apply {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        })
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

    val lastLocation = Location(BuildConfig.APPLICATION_ID)

    override fun onBindViewHolder(holder: EventHolder, position: Int) =
        holder.bindTo(getItem(position), lastLocation, listener)

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

    private val lastLocation: Location = Location(BuildConfig.APPLICATION_ID)

    fun bindTo(
        event: Event?,
        lastLocation: Location,
        listener: (Event) -> Unit
    ) {
        if (event != null) {
            this.event = event.apply {
                displayTime()
                displayLocation()
                displayMeasurementTypes()
            }

            itemView.setBackgroundColor(Prefs.backgroundColor)
            itemView.setOnClickListener { listener(event) }
        }

        this.lastLocation.apply {
            latitude = lastLocation.latitude
            longitude = lastLocation.longitude
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

        val hasLocationPermission = context.hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)
        if (hasLocationPermission) {
            locationIconView.setIcon(
                icon = GoogleMaterial.Icon.gmd_location_on,
                color = Prefs.textColor
            )
            locationView.apply {
                text = location.distanceTo(lastLocation).distanceToString(context)
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
        measurementsView.removeAllViews()
        measurements.toHashSet().forEach { measurement ->
            measurementsView.addView(ImageView(itemView.context).apply {
                setImageResource(measurement.type.iconRes)
                setColorFilter(Prefs.textColor, PorterDuff.Mode.SRC_IN)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
            })
        }
    }

}