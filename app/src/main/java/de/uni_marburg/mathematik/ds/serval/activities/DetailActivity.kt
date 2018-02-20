package de.uni_marburg.mathematik.ds.serval.activities

import android.content.Intent
import android.os.Bundle
import androidx.net.toUri
import ca.allanwang.kau.iitems.CardIItem
import ca.allanwang.kau.ui.activities.ElasticRecyclerActivity
import ca.allanwang.kau.utils.*
import com.mikepenz.fastadapter.IItem
import com.mikepenz.fastadapter.commons.adapters.FastItemAdapter
import com.mikepenz.google_material_typeface_library.GoogleMaterial
import de.uni_marburg.mathematik.ds.serval.R
import de.uni_marburg.mathematik.ds.serval.enums.SupportTopic
import de.uni_marburg.mathematik.ds.serval.model.event.Event
import de.uni_marburg.mathematik.ds.serval.model.event.EventDatabase
import de.uni_marburg.mathematik.ds.serval.utils.*
import de.uni_marburg.mathematik.ds.serval.views.MapIItem
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.util.*

/**
 * Displays all details of an [event][Event].
 */
class DetailActivity : ElasticRecyclerActivity() {

    private lateinit var event: Event

    override fun onCreate(savedInstanceState: Bundle?, configs: Configs): Boolean {
        setSecureFlag()
        setColors {
            header(appBar)
            toolbar(toolbar)
            themeWindow = false
        }

        doAsync {
            event = EventDatabase
                .get(this@DetailActivity)
                .eventDao()
                .getById(intent.extras.getString(EVENT_ID))

            uiThread {
                setupAdapter()
                fab.apply {
                    setIcon(icon = GoogleMaterial.Icon.gmd_navigation, color = Prefs.iconColor)
                    setOnClickListener { showInGoogleMaps() }
                    show()
                }
            }
        }

        setOutsideTapListener { finishAfterTransition() }

        return true
    }

    private fun setupAdapter() {
        val adapter = FastItemAdapter<IItem<*, *>>()
        val showMap: Boolean = intent.extras.getBoolean(SHOW_MAP)

        if (::event.isInitialized) {
            title = event.title

            adapter.apply {
                if (showMap) add(MapIItem(event))

                val eventCardItem = CardIItem {
                    titleRes = R.string.time
                    val passedTime: String = event.passedTime.timeToString(this@DetailActivity)
                    desc = "${event.snippet}\n$passedTime"
                    imageIIcon = GoogleMaterial.Icon.gmd_access_time
                }
                add(eventCardItem)

                event.measurements.map { measurement ->
                    val measurementCardItem = CardIItem {
                        titleRes = measurement.type.titleRes
                        desc = String.format(string(measurement.type.formatRes), measurement.value)
                        imageIIcon = measurement.type.iicon
                        imageIIconColor = Prefs.iconColor
                    }
                    add(measurementCardItem)
                }
            }
        } else {
            title = string(R.string.event_missing)
            adapter.apply {
                val missingEventCard = CardIItem {
                    descRes = R.string.event_missing_description
                    buttonRes = R.string.report_bug
                    buttonClick = { SupportTopic.BUG.sendEmail(this@DetailActivity) }
                }
                add(missingEventCard)
            }
        }

        recycler.adapter = adapter
    }

    private fun showInGoogleMaps() {
        if (isAppInstalled(GOOGLE_MAPS)) {
            if (isAppEnabled(GOOGLE_MAPS)) {
                val action: String = Intent.ACTION_VIEW

                val uriFormat: String = string(R.string.intent_uri_show_in_google_maps)
                val uriValues = arrayOf(
                    // English localization forces dot delimeter
                    String.format(Locale.ENGLISH, "%.5f", event.location.latitude),
                    String.format(Locale.ENGLISH, "%.5f", event.location.longitude),
                    event.title
                )
                val uri = String.format(uriFormat, *uriValues).toUri()

                val navigationIntent = Intent(action, uri).apply { `package` = GOOGLE_MAPS }
                startActivity(navigationIntent)
            } else snackbarThemed(
                textRes = R.string.google_maps_not_enabled,
                builder = { setAction(R.string.enable, { showAppInfo(GOOGLE_MAPS) }) }
            )
        } else snackbarThemed(
            textRes = R.string.google_maps_not_installed,
            builder = { setAction(R.string.install, { startPlayStoreLink(GOOGLE_MAPS) }) }
        )
    }

    companion object {
        const val EVENT_ID = "EVENT_ID"
        const val SHOW_MAP = "SHOW_MAP"
    }
}
