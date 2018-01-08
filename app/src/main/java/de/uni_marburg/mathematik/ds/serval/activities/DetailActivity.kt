package de.uni_marburg.mathematik.ds.serval.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.drawable
import ca.allanwang.kau.utils.string
import ca.allanwang.kau.utils.tint
import ca.allanwang.kau.utils.toDrawable
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import de.uni_marburg.mathematik.ds.serval.utils.setCurrentScreen
import de.uni_marburg.mathematik.ds.serval.utils.setSecureFlag
import de.uni_marburg.mathematik.ds.serval.utils.timeToString
import de.uni_marburg.mathematik.ds.serval.views.MapIItem
import java.util.*

/** Displays all details of an [event][Event]. */
class DetailActivity : ElasticRecyclerActivity() {

    private val adapter = FastItemAdapter<IItem<*, *>>()

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        setSecureFlag()
        setCurrentScreen()
        event = intent.extras.getParcelable(EVENT)
        title = event.title
        recycler.adapter = setupAdapter()
        fab.apply {
            setImageDrawable(GoogleMaterial.Icon.gmd_navigation.toDrawable(context))
            setOnClickListener { showInGoogleMaps() }
            show()
        }
        setOutsideTapListener { finishAfterTransition() }
        return true
    }

    private fun setupAdapter(): FastItemAdapter<IItem<*, *>> {
        val showMap: Boolean = intent.extras.getBoolean(SHOW_MAP)
        if (showMap) adapter.add(MapIItem(event))
        adapter.add(CardIItem {
            val timeDifference = Calendar.getInstance().timeInMillis - event.time
            titleRes = R.string.time
            desc = "${event.snippet}\n${timeDifference.timeToString(this@DetailActivity)}"
            imageIIcon = GoogleMaterial.Icon.gmd_access_time
        })
        event.measurements.map {
            adapter.add(CardIItem {
                titleRes = it.type.res
                desc = String.format(string(it.type.resFormat), it.value)
                image = drawable(it.type.resId).tint(Prefs.iconColor)
            })
        }
        return adapter
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
        const val SHOW_MAP = "SHOW_MAP"
    }
}
