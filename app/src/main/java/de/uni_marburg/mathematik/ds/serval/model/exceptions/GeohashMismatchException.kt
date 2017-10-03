package de.uni_marburg.mathematik.ds.serval.model.exceptions

/** Is thrown when latitude, longitude and geohash don't match */
class GeohashMismatchException(message: String) : Exception(message) {

    companion object {

        private val serialVersionUID = 1L
    }
}
