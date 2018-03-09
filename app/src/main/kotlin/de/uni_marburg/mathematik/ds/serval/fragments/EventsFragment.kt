package de.uni_marburg.mathematik.ds.serval.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.arch.paging.PagedListAdapter
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import ca.allanwang.kau.animators.FadeScaleAnimatorAdd
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.animators.NoAnimatorChange
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.activities.MainActivity
import de.uni_marburg.mathematik.ds.serval.model.Event
import de.uni_marburg.mathematik.ds.serval.model.EventComparator
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.*
import de.uni_marburg.mathematik.ds.serval.model.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread


class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    val recyclerView by bindView<RecyclerView>(R.id.recycler_view)

    private val swipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_refresh)

    private val eventAdapter: EventAdapter by lazy {
        val context: Context = requireContext()
        EventAdapter { event ->
            context.startActivity<DetailActivity>(
                bundleBuilder = { if (Prefs.animate) withSceneTransitionAnimation(context) },
                intentBuilder = {
                    putExtra(DetailActivity.EVENT_ID, event.id)
                    putExtra(DetailActivity.SHOW_MAP, true)
                }
            )
        }
    }

    private val eventViewModel: EventViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(EventViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        eventViewModel.events.observe(requireActivity(), Observer(eventAdapter::submitList))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupRefresh()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_events, menu)

        with(requireContext()) {
            setMenuIcons(
                menu = menu,
                color = Prefs.iconColor,
                iicons = *arrayOf(R.id.action_sort_events to GoogleMaterial.Icon.gmd_filter_list)
            )
            menu.findItem(R.id.action_sort_distance).isVisible = hasLocationPermission
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_distance_asc -> sortEventsBy(Distance)
            R.id.action_sort_distance_desc -> sortEventsBy(Distance, reversed = true)
            R.id.action_sort_measurements_asc -> sortEventsBy(Measurements)
            R.id.action_sort_measurements_desc -> sortEventsBy(Measurements, reversed = true)
            R.id.action_sort_time_asc -> sortEventsBy(Time)
            R.id.action_sort_time_desc -> sortEventsBy(Time, reversed = true)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun reloadEvents() {
        with(swipeRefreshLayout) {
            if (context.isNetworkAvailable) {
                isRefreshing = true
                doAsync {
                    eventViewModel.reload()
                    uiThread { isRefreshing = false }
                }
            } else {
                isRefreshing = false
                snackbarThemed(context.string(R.string.network_disconnected))
            }
            return
        }
    }

    private fun sortEventsBy(eventComparator: EventComparator, reversed: Boolean = false) {
        swipeRefreshLayout.isRefreshing = true
        doAsync {
            eventViewModel.sortEventsBy(eventComparator, reversed)
            uiThread { swipeRefreshLayout.isRefreshing = false }
        }
    }

    private fun setupRecyclerView() {
        with(recyclerView) {
            withLinearAdapter(eventAdapter)
            if (Prefs.animate) {
                itemAnimator = KauAnimator(
                    addAnimator = FadeScaleAnimatorAdd(scaleFactor = 0.7f, itemDelayFactor = 0.2f),
                    changeAnimator = NoAnimatorChange()
                ).apply {
                    addDuration = 300
                    interpolator = AnimHolder.decelerateInterpolator(context)

                }
            }
        }
    }

    private fun setupRefresh() = swipeRefreshLayout.setOnRefreshListener { reloadEvents() }

    private class EventAdapter(
        private val listener: (Event) -> Unit
    ) : PagedListAdapter<Event, EventAdapter.EventHolder>(diffCallback) {

        companion object {
            private val diffCallback = object : DiffUtil.ItemCallback<Event>() {
                override fun areContentsTheSame(
                    oldEvent: Event,
                    newEvent: Event
                ): Boolean = oldEvent == newEvent

                override fun areItemsTheSame(
                    oldEvent: Event,
                    newEvent: Event
                ): Boolean = oldEvent.id == newEvent.id
            }
        }

        override fun onBindViewHolder(holder: EventHolder, position: Int) {
            val event: Event? = currentList?.get(position)
            if (event != null) holder.bindTo(event, listener)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): EventHolder = EventHolder(parent)

        private class EventHolder(
            parent: ViewGroup
        ) : RecyclerView.ViewHolder(parent.inflate(R.layout.event_row)) {

            private val timeView: TextView by bindView(R.id.time)
            private val measurementsView: LinearLayout by bindView(R.id.measurement_types)
            private val locationIconView: ImageView by bindView(R.id.location_icon)
            private val guideline: Guideline by bindView(R.id.guideline)
            private val locationView: TextView by bindView(R.id.location)

            fun bindTo(event: Event, listener: (Event) -> Unit) {
                with(event) {
                    displayTime()
                    displayMeasurementTypes()
                    displayLocation()

                    itemView.setOnClickListener { listener(this) }
                }
            }

            private fun Event.displayTime() {
                with(timeView) {
                    text = passedSeconds.formatPassedSeconds(itemView.context)
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
                    with(locationView) {
                        val distance: Float = location.distanceTo(MainActivity.lastLocation)
                        text = distance.formatDistance(context)
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
                    measurements.distinct().forEach { measurement ->
                        val icon = ImageView(itemView.context).apply {
                            setIcon(icon = measurement.type.iicon, color = Prefs.textColor)
                        }
                        addView(icon)
                    }
                }
            }

        }

    }

}