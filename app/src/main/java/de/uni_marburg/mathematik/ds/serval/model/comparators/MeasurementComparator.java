package de.uni_marburg.mathematik.ds.serval.model.comparators;

import java.util.Comparator;

import de.uni_marburg.mathematik.ds.serval.model.event.Measurement;

/**
 * Created by thames1990 on 10.09.17.
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
