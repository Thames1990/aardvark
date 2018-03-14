package de.uni_marburg.mathematik.ds.serval.model

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import de.uni_marburg.mathematik.ds.serval.settings.ServalPrefs
import kerval.ServalClient
import java.io.BufferedReader

/**
 * Entry point to Kerval API.
 *
 * Loads [events][Event] over HTTP and reads them from pseudo-JSON formatted input.
 */
object EventRepository {

    /**
     * Authenticates with the Serval API and offers connection to the Rhizome database.
     */
    private val client = ServalClient(
        host = ServalPrefs.baseUrl,
        port = ServalPrefs.port,
        user = ServalPrefs.user,
        password = ServalPrefs.password
    )

    /**
     * JSON converter
     */
    val moshi: Moshi = Moshi.Builder().build()

    /**
     * Json converter adapter for [events][Event]
     */
    private val eventAdapter: JsonAdapter<Event> = moshi.adapter(Event::class.java)

    /**
     * Fetches [a number of][count] [events][Event] from the [Serval client][client].
     */
    fun fetch(count: Int = ServalPrefs.eventCount): List<Event> {
        val events = mutableListOf<Event>()

        with(client.rhizome) {
            bundleList.forEach { bundle ->
                val bundleId: String? = bundle.bundleId

                if (bundleId != null) {
                    val raw: String = getRaw(bundleId)
                    val rawReader: BufferedReader = raw.byteInputStream().bufferedReader()

                    rawReader.forEachLine { line ->
                        val event: Event? = eventAdapter.fromJson(line)
                        if (event != null) events.add(event)
                    }
                }
            }
        }

        return events.take(count)
    }

}