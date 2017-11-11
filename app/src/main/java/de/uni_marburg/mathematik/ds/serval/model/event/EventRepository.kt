package de.uni_marburg.mathematik.ds.serval.model.event

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import de.uni_marburg.mathematik.ds.serval.util.Preferences
import kerval.ServalClient
import kerval.rhizome.Bundle

/**
 * Entry point to Kerval API.
 *
 * Loads [events][Event] over HTTP and reads the from pseudo-JSON formatted input.
 */
object EventRepository {

    /**
     * Authenticates with the Serval API and offers connection to the Rhizome database.
     */
    private val client: ServalClient by lazy {
        ServalClient(
                Preferences.kervalBaseUrl,
                Preferences.kervalPort,
                Preferences.kervalUser,
                Preferences.kervalPassword
        )
    }

    /**
     * JSON converter
     */
    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /**
     * Json converter adapter for [events][Event]
     */
    private val eventAdapter: JsonAdapter<Event> = moshi.adapter(Event::class.java)

    /**
     * Fetches [events][Event] from the [Serval client][client].
     */
    fun fetch(): MutableList<Event> {
        val events = mutableListOf<Event>()
        with(client.rhizome) {
            getBundleList().forEach { bundle: Bundle ->
                bundle.bundleId?.let { bid ->
                    getRaw(bid).byteInputStream().bufferedReader().forEachLine { line ->
                        // This check is only necessary because the JSON contains duplicate newlines
                        if (line.isNotEmpty()) eventAdapter.fromJson(line)?.let { event ->
                            events.add(event)
                        }
                    }
                }
            }
        }
        return events
    }

}