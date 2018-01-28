package de.uni_marburg.mathematik.ds.serval.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.setIcon
import ca.allanwang.kau.utils.string
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventDatabase
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.MapIItem
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/** Displays all details of an [event][Event]. */
class DetailActivity : ElasticRecyclerActivity() {

    private val adapter = FastItemAdapter<IItem<*, *>>()

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        setSecureFlag()
        setCurrentScreen()

        setAardvarkColors {
            toolbar(toolbar)
            themeWindow = false
            header(appBar)
        }

        doAsync {
            event = EventDatabase
                .get(this@DetailActivity)
                .eventDao()
                .getById(intent.extras.getLong(EVENT_ID))
            uiThread {
                title = event.title
                recycler.adapter = setupAdapter()
                fab.apply {
                    setIcon(icon = GoogleMaterial.Icon.gmd_navigation, color = Prefs.iconColor)
                    setOnClickListener { showInGoogleMaps() }
                    show()
                }
                setOutsideTapListener { finishAfterTransition() }
            }
        }

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

        event.measurements.map { measurement ->
            adapter.add(CardIItem {
                titleRes = measurement.type.titleRes
                desc = String.format(string(measurement.type.formatRes), measurement.value)
                imageIIcon = measurement.type.iicon
                imageIIconColor = Prefs.iconColor
            })
        }

        return adapter
    }

    private fun showInGoogleMaps() {
        val navigationIntent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse(
                String.format(
                    string(R.string.intent_uri_show_in_google_maps),
                    // Forces decimal points
                    String.format(Locale.ENGLISH, "%.5f", event.location.latitude),
                    String.format(Locale.ENGLISH, "%.5f", event.location.longitude),
                    event.title
                )
            )
        )
        navigationIntent.`package` = "com.google.android.apps.maps"
        startActivity(navigationIntent)
    }

    companion object {
        const val EVENT_ID = "EVENT_ID"
        const val SHOW_MAP = "SHOW_MAP"
    }
}
