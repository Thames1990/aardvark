package de.uni_marburg.mathematik.ds.serval.view.activities

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.setBackgroundColorRes
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.tint
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.util.Prefs
import de.uni_marburg.mathematik.ds.serval.util.consume
import de.uni_marburg.mathematik.ds.serval.util.setCurrentScreen
import de.uni_marburg.mathematik.ds.serval.util.setSecureFlag
import de.uni_marburg.mathematik.ds.serval.view.views.MapIItem
import java.util.*

/** Displays all details of an [event][Event]. */
class DetailActivity : ElasticRecyclerActivity() {

    private val adapter = FastItemAdapter<IItem<*, *>>()

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean = consume {
        setSecureFlag()
        setCurrentScreen()
        toolbar.setBackgroundColorRes(Prefs.colorPrimary)
        event = intent.extras.getParcelable(EVENT)
        title = event.title
        recycler.adapter = adapter
        setupAdapter()
        fab.apply {
            setImageResource(R.drawable.navigation)
            setOnClickListener { showInGoogleMaps() }
            show()
        }
        setOutsideTapListener { finishAfterTransition() }
    }

    private fun setupAdapter() {
        adapter.add(MapIItem(event))
        adapter.add(CardIItem {
            titleRes = R.string.time
            desc = event.snippet
            image = drawable(R.drawable.time).tint(Color.WHITE)
        })
        event.measurements.forEach { measurement ->
            adapter.add(CardIItem {
                titleRes = measurement.type.res
                desc = String.format(string(measurement.type.resFormat), measurement.value)
                image = drawable(measurement.type.resId).tint(Color.WHITE)
            })
        }
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
