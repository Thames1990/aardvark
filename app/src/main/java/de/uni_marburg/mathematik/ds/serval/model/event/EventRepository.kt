package de.uni_marburg.mathematik.ds.serval.model.event

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import kerval.ServalClient
import kerval.rhizome.Bundle

/**
 * Entry point to Kerval API.
 *
 * Loads [events][Event] over HTTP and reads the from pseudo-JSON formatted input.
 */
object EventRepository {

    /** Authenticates with the Serval API and offers connection to the Rhizome database. */
    private val client: ServalClient by lazy {
        ServalClient(
            Prefs.kervalBaseUrl,
            Prefs.kervalPort,
            Prefs.kervalUser,
            Prefs.kervalPassword
        )
    }

    /** JSON converter */
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /** Json converter adapter for [events][Event] */
    private val eventAdapter: JsonAdapter<Event> = moshi.adapter(Event::class.java)

    /** Fetches [a number of][count] [events][Event] from the [Serval client][client]. */
    fun fetch(count: Int = Prefs.eventCount): List<Event> {
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
        return events.take(count)
    }

}