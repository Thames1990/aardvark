package de.uni_marburg.mathematik.ds.serval.model.util

/** Is used to check if latitude, longitude and geohash match. */
object GeohashUtil {

    private val BASE_32 = "0123456789bcdefghjkmnpqrstuvwxyz"

    private fun divideRangeByValue(value: Double, range: DoubleArray): Int {
        val mid = middle(range)
        return if (value >= mid) {
            range[0] = mid
            1
        } else {
            range[1] = mid
            0
        }
    }

    private fun divideRangeByBit(bit: Int, range: DoubleArray) {
        val mid = middle(range)
        if (bit > 0) {
            range[0] = mid
        } else {
            range[1] = mid
        }
    }

    private fun middle(range: DoubleArray): Double = (range[0] + range[1]) / 2

    fun encodeGeohash(latitude: Double, longitude: Double, precision: Int): String {
        val latRange = doubleArrayOf(-90.0, 90.0)
        val lonRange = doubleArrayOf(-180.0, 180.0)
        var isEven = true
        var bit = 0
        var base32CharIndex = 0
        val geohash = StringBuilder()

        while (geohash.length < precision) {
            if (isEven) {
                base32CharIndex = base32CharIndex shl 1 or divideRangeByValue(longitude, lonRange)
            } else {
                base32CharIndex = base32CharIndex shl 1 or divideRangeByValue(latitude, latRange)
            }

            isEven = !isEven

            if (bit < 4) {
                bit++
            } else {
                geohash.append(BASE_32[base32CharIndex])
                bit = 0
                base32CharIndex = 0
            }
        }

        return geohash.toString()
    }

    fun decodeGeohash(geohash: String): DoubleArray {
        val latRange = doubleArrayOf(-90.0, 90.0)
        val lonRange = doubleArrayOf(-180.0, 180.0)
        var isEvenBit = true

        for (i in 0 until geohash.length) {
            val base32CharIndex = BASE_32.indexOf(geohash[i])
            for (j in 4 downTo 0) {
                if (isEvenBit) {
                    divideRangeByBit(base32CharIndex shr j and 1, lonRange)
                } else {
                    divideRangeByBit(base32CharIndex shr j and 1, latRange)
                }
                isEvenBit = !isEvenBit
            }
        }

        return doubleArrayOf(middle(latRange), middle(lonRange))
    }
}
