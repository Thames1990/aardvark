package de.uni_marburg.mathematik.ds.serval.fragments

import android.arch.paging.PagedListAdapter
import android.content.Context
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.ViewGroup
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
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.ASCENDING
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.DESCENDING
import de.uni_marburg.mathematik.ds.serval.model.Measurement
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.utils.*


class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)
    private val swipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_refresh)

    private val eventAdapter = EventAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        observe(liveData = eventViewModel.pagedList, body = eventAdapter::submitList)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_events, menu)

        with(requireContext()) {
            setMenuIcons(
                menu = menu,
                color = AppearancePrefs.Theme.iconColor,
                iicons = *arrayOf(R.id.action_sort_events to GoogleMaterial.Icon.gmd_sort)
            )
            menu.findItem(R.id.action_sort_distance).isVisible = hasLocationPermission
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_sort_distance_asc -> sortEventsBy(Distance)
            R.id.action_sort_distance_desc -> sortEventsBy(Distance, order = DESCENDING)
            R.id.action_sort_measurements_asc -> sortEventsBy(Measurements)
            R.id.action_sort_measurements_desc -> sortEventsBy(Measurements, order = DESCENDING)
            R.id.action_sort_time_asc -> sortEventsBy(Time)
            R.id.action_sort_time_desc -> sortEventsBy(Time, order = DESCENDING)
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    fun reloadEvents() {
        with(swipeRefreshLayout) {
            if (context.isNetworkAvailable) {
                isRefreshing = true
                eventViewModel.getFromRepository(doOnFinish = { isRefreshing = false })
            } else {
                isRefreshing = false
                snackbarThemed(context.string(R.string.network_disconnected))
            }
            return
        }
    }

    fun bindFab(fab: FloatingActionButton) = fab.hideOnDownwardsScroll(recyclerView)

    fun scrollToTop() = recyclerView.scrollToPosition(0)

    private fun sortEventsBy(eventComparator: EventComparator, order: Order = ASCENDING) {
        swipeRefreshLayout.isRefreshing = true
        eventViewModel.sortBy(
            eventComparator,
            order,
            doOnFinish = { swipeRefreshLayout.isRefreshing = false }
        )
    }

    private fun setupRecyclerView() {
        swipeRefreshLayout.setOnRefreshListener { reloadEvents() }
        with(recyclerView) {
            withLinearAdapter(eventAdapter)
            if (animationsAreEnabled) itemAnimator = KauAnimator(
                addAnimator = FadeScaleAnimatorAdd(scaleFactor = 0.7f, itemDelayFactor = 0.2f),
                changeAnimator = NoAnimatorChange()
            ).apply {
                addDuration = 300
                interpolator = AnimHolder.decelerateInterpolator(context)

            }
        }
    }

    private class EventAdapter : PagedListAdapter<Event, EventAdapter.ViewHolder>(diffCallback) {

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val event: Event? = currentList?.get(position)
            event?.let { holder.bindTo(it) }
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder = ViewHolder(parent)

        private class ViewHolder(
            parent: ViewGroup
        ) : RecyclerView.ViewHolder(parent.inflate(R.layout.event_row)) {

            private val timeView: TextView by bindView(R.id.time)
            private val measurementsView: LinearLayout by bindView(R.id.measurement_types)
            private val locationIconView: ImageView by bindView(R.id.location_icon)
            private val guideline: Guideline by bindView(R.id.guideline)
            private val locationView: TextView by bindView(R.id.location)

            private lateinit var event: Event

            fun bindTo(event: Event) {
                this.event = event

                setupTimeView()
                setupMeasurementsView()
                setupLocationView()

                itemView.setOnClickListener {
                    val context: Context = it.context
                    context.startActivity<DetailActivity>(
                        bundleBuilder = {
                            if (animationsAreEnabled) withSceneTransitionAnimation(context)
                        },
                        intentBuilder = {
                            putExtra(DetailActivity.EVENT_ID, event.id)
                            putExtra(DetailActivity.SHOULD_SHOW_MAP, true)
                        }
                    )
                }
            }

            private fun setupTimeView() = with(timeView) {
                text = event.passedSeconds.formatPassedSeconds(itemView.context)
                setTextColor(AppearancePrefs.Theme.textColor)
            }

            private fun setupLocationView() {
                fun show() {
                    locationIconView.setIcon(
                        icon = GoogleMaterial.Icon.gmd_location_on,
                        color = AppearancePrefs.Theme.textColor
                    )
                    with(locationView) {
                        val distance: Float = event.location.distanceTo(MainActivity.deviceLocation)
                        text = distance.formatDistance(itemView.context)
                        setTextColor(AppearancePrefs.Theme.textColor)
                    }
                }

                fun hide() {
                    locationIconView.gone()
                    locationView.gone()
                    guideline.updateLayoutParams<ConstraintLayout.LayoutParams> { guideEnd = 0 }
                }

                if (itemView.context.hasLocationPermission) show() else hide()
            }

            private fun setupMeasurementsView() = with(measurementsView) {
                removeAllViews()

                val uniqueMeasurements: List<Measurement> = event.measurements.distinct()
                uniqueMeasurements.forEach { measurement ->
                    val icon = ImageView(itemView.context).apply {
                        setIcon(
                            icon = measurement.type.iicon,
                            color = AppearancePrefs.Theme.textColor
                        )
                    }
                    addView(icon)
                }
            }

        }

        companion object {
            private val diffCallback = object : DiffUtil.ItemCallback<Event>() {
                override fun areContentsTheSame(old: Event, new: Event): Boolean = old == new
                override fun areItemsTheSame(old: Event, new: Event): Boolean = old.id == new.id
            }
        }

    }

}