package de.uni_marburg.mathematik.ds.serval.fragments

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.RecyclerView
import android.view.*
import ca.allanwang.kau.animators.KauAnimator
import ca.allanwang.kau.utils.*
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.activities.DetailActivity
import de.uni_marburg.mathematik.ds.serval.model.event.EventAdapter
import de.uni_marburg.mathematik.ds.serval.model.event.EventViewModel
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.aardvarkSnackbar
import de.uni_marburg.mathematik.ds.serval.utils.withDividerDecoration
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.startActivity
import org.jetbrains.anko.uiThread


class EventsFragment : BaseFragment() {

    override val layout: Int
        get() = R.layout.fragment_events

    private val swipeRefreshLayout by bindView<SwipeRefreshLayout>(R.id.swipe_refresh)
    private val recyclerView by bindView<RecyclerView>(R.id.recycler_view)

    private val eventAdapter = EventAdapter {
        context!!.startActivity<DetailActivity>(
            DetailActivity.EVENT_ID to it.id,
            DetailActivity.SHOW_MAP to true
        )
    }

    private val eventViewModel by lazy(LazyThreadSafetyMode.NONE) {
        ViewModelProviders.of(activity!!).get(EventViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        eventViewModel.allEvents.observe(this, Observer(eventAdapter::setList))
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
