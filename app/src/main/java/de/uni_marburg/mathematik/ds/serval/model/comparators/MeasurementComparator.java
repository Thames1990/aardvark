package de.uni_marburg.mathematik.ds.serval.model.comparators;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;

/**
 * Compares {@link Measurement measurements} based on their {@link Measurement#type type} and {@link
 * Measurement#value value}.
 */
public class MeasurementComparator implements Comparator<Measurement> {
    
    @Override
    public int compare(Measurement measurement, Measurement t1) {
        if (measurement.getType().equals(t1.getType())) {
            return measurement.getType().compareTo(measurement.getType());
        }
        return measurement.getValue().compareTo(t1.getValue());
    }
}
