package de.uni_marburg.mathematik.ds.serval.model.event

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.KotlinJsonAdapterFactory
import com.squareup.moshi.Moshi
import de.uni_marburg.mathematik.ds.serval.util.randomRange
import okhttp3.*
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

object EventProvider {

    private val client = OkHttpClient()

    private val request = Request.Builder().url("https://api.splork.de/dummy_data.json").build()

    private val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

    private val eventAdapter: JsonAdapter<Event> = moshi.adapter(Event::class.java)

    private fun randomDouble(from: Double = 0.0, to: Double = Double.MAX_VALUE): Double =
            Math.random() * (to - from) + from

    private fun randomInt(from: Int = 0, to: Int = Int.MAX_VALUE): Int =
            (Math.random() * (to - from) + from).toInt()

    private fun randomLong(from: Long = 0, to: Long = Long.MAX_VALUE): Long =
            (Math.random() * (to - from) + from).toLong()

    fun generate(): List<Event> = (1..1000).map {
        Event(
                Calendar
                        .getInstance()
                        .timeInMillis
                        .minus(randomLong(to = TimeUnit.DAYS.toMillis(30))),
                GeohashLocation(
                        randomDouble(to = 90.0),
                        randomDouble(-180.0, 180.0),
                        UUID.randomUUID().toString().replace("-", "")
                ),
                (1..10).randomRange().map {
                    Measurement(
                            MeasurementType.values()[randomInt(to = MeasurementType.values().size)],
                            // Dont infuriate Vegeta
                            randomInt().coerceAtMost(9000)
                    )
                }
        )
    }

    fun load(): List<Event>? {
        val events = mutableListOf<Event>()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) = Unit

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val bufferedReader = response.body()!!.byteStream().bufferedReader()
                    bufferedReader.useLines { lines ->
                        lines.forEach {
                            events.add(eventAdapter.fromJson(it)!!)
                        }
                    }
                }
            }
        })

        return events
    }

}