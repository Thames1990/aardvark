package de.uni_marburg.mathematik.ds.serval.model.exceptions;

/**
 * Is thrown when latitude, longitude and geohash don't match
 */
public class GeohashMismatchException extends Exception {

    private static final long serialVersionUID = 1L;

    public GeohashMismatchException() {
    }

    public GeohashMismatchException(String message) {
        super(message);
    }
}
