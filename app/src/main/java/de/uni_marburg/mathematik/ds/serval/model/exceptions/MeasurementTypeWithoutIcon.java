package de.uni_marburg.mathematik.ds.serval.model.exceptions;

/**
 *
 */
public class MeasurementTypeWithoutIcon extends Exception {

    static final long serialVersionUID = 1L;

    public MeasurementTypeWithoutIcon() {
    }

    public MeasurementTypeWithoutIcon(String message) {
        super(message);
    }

    public MeasurementTypeWithoutIcon(String message, Throwable cause) {
        super(message, cause);
    }
}
