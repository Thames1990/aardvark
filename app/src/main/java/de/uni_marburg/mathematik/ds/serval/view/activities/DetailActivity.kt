package de.uni_marburg.mathematik.ds.serval.view.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.WindowManager
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.view.views.MapIItem
import java.util.*

/** Displays all details of an [event][Event]. */
class DetailActivity : ElasticRecyclerActivity() {

    private val adapter = FastItemAdapter<IItem<*, *>>()

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean = consume {
        window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
        toolbar.setBackgroundColorRes(R.color.color_primary)
        event = intent.extras.getParcelable(EVENT)
        title = event.title
        recycler.adapter = adapter
        adapter.add(MapIItem(event))
        adapter.add(CardIItem {
            titleRes = R.string.time
            desc = event.snippet
            image = drawable(R.drawable.time).tint(color(android.R.color.white))
        })
        event.measurements.forEach { measurement ->
            adapter.add(CardIItem {
                titleRes = measurement.type.res
                desc = String.format(string(measurement.type.resFormat), measurement.value)
                image = drawable(measurement.type.resId).tint(color(android.R.color.white))
            })
        }
        fab.apply {
            setImageResource(R.drawable.navigation)
            setOnClickListener { showInGoogleMaps() }
            show()
        }
        setOutsideTapListener { finishAfterTransition() }
    }

    private fun showInGoogleMaps() {
        val navigationIntent = Intent(
                Intent.ACTION_VIEW,
                Uri.parse(String.format(
                        string(R.string.intent_uri_show_in_google_maps),
                        // Forces decimal points
                        String.format(Locale.ENGLISH, "%.5f", event.location.latitude),
                        String.format(Locale.ENGLISH, "%.5f", event.location.longitude),
                        event.title
                ))
        )
        navigationIntent.`package` = "com.google.android.apps.maps"
        startActivity(navigationIntent)
    }

    companion object {

        const val EVENT = "EVENT"
    }
}
