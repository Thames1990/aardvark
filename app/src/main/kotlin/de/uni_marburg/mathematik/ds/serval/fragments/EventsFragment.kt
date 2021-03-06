package de.uni_marburg.mathematik.ds.serval.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.constraint.Guideline
import android.support.design.widget.AppBarLayout
import android.support.design.widget.AppBarLayout.LayoutParams.*
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
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
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.ASCENDING
import de.uni_marburg.mathematik.ds.serval.model.EventComparator.Order.DESCENDING
import de.uni_marburg.mathematik.ds.serval.model.Measurement
import de.uni_marburg.mathematik.ds.serval.settings.AppearancePrefs
import de.uni_marburg.mathematik.ds.serval.utils.*


class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val recyclerView by bindViewResettable<RecyclerView>(R.id.recycler_view)
    private val swipeRefreshLayout by bindViewResettable<SwipeRefreshLayout>(R.id.swipe_refresh)

    private val eventAdapter = EventAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        observe(liveData = eventViewModel.events, onChanged = eventAdapter::submitList)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupRecyclerView()
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) = createOptionsMenu(
        menu = menu,
        inflater = inflater,
        menuRes = R.menu.menu_events,
        iicons = *arrayOf(R.id.action_sort_events to GoogleMaterial.Icon.gmd_sort),
        block = {
            val sortDistanceMenuItem: MenuItem? = menu?.findItem(R.id.action_sort_distance)
            sortDistanceMenuItem?.isVisible = requireContext().hasLocationPermission
        }
    )

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        item ?: return false
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

    override fun onSelected(
        appBarLayout: AppBarLayout,
        toolbar: Toolbar,
        fab: FloatingActionButton
    ) {
        appBarLayout.expand()
        with(toolbar) {
            updateLayoutParams<AppBarLayout.LayoutParams> {
                scrollFlags = SCROLL_FLAG_SCROLL or SCROLL_FLAG_ENTER_ALWAYS or SCROLL_FLAG_SNAP
            }
            title = context.string(R.string.tab_item_events)
        }
        with(fab) {
            hideOnDownwardsScroll(recyclerView)
            showWithOptions(
                icon = GoogleMaterial.Icon.gmd_arrow_upward,
                tooltipTextRes = R.string.tooltip_fab_scroll_to_top,
                onClickListener = {
                    appBarLayout.expand()
                    recyclerView.scrollToPosition(0)
                }
            )
        }
    }

    override fun onReselected() = reloadEvents()

    private fun reloadEvents(deleteEvents: Boolean = false): Unit = with(swipeRefreshLayout) {
        if (context.isNetworkAvailable) {
            isRefreshing = true
            eventViewModel.getFromRepository(
                deleteEvents = deleteEvents,
                doOnFinish = { isRefreshing = false }
            )
        } else {
            isRefreshing = false
            snackbarThemed(context.string(R.string.network_disconnected))
        }
        return
    }

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

    private class EventAdapter : ListAdapter<Event, EventAdapter.ViewHolder>(diffCallback) {

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
        ): ViewHolder = ViewHolder(parent.inflate(R.layout.event_row))

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int
        ) = holder.bindTo(getItem(position))

        private class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

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

                with(itemView) {
                    setOnClickListener {
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
                override fun areItemsTheSame(old: Event, new: Event): Boolean = old.id == new.id
                override fun areContentsTheSame(old: Event, new: Event): Boolean = old == new
            }
        }

    }

}