package de.uni_marburg.mathematik.ds.serval.model.event

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import de.uni_marburg.mathematik.ds.serval.utils.Prefs
import kerval.ServalClient
import java.io.BufferedReader

/**
 * Entry point to Kerval API.
 *
 * Loads [events][Event] over HTTP and reads the from pseudo-JSON formatted input.
 */
object EventRepository {

    /** Authenticates with the Serval API and offers connection to the Rhizome database. */
    private val client = ServalClient(
        Prefs.kervalBaseUrl,
        Prefs.kervalPort,
        Prefs.kervalUser,
        Prefs.kervalPassword
    )

    /** JSON converter */
    val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    /** Json converter adapter for [events][Event] */
    private val eventAdapter: JsonAdapter<Event> = moshi.adapter(Event::class.java)

    /** Fetches [a number of][count] [events][Event] from the [Serval client][client]. */
    fun fetch(count: Int = Prefs.eventCount): List<Event> {
        val events = mutableListOf<Event>()

        with(client.rhizome) {
            bundleList.forEach { bundle ->
                val bundleId: String? = bundle.bundleId

                if (bundleId != null) {
                    val raw: String = getRaw(bundleId)
                    val rawReader: BufferedReader = raw.byteInputStream().bufferedReader()

                    rawReader.forEachLine { line ->
                        if (line.isNotEmpty()) {
                            val event: Event? = eventAdapter.fromJson(line)
                            if (event != null) events.add(event)
                        }
                    }
                }
            }
        }

        return events.take(count)
    }

}