package de.uni_marburg.mathematik.ds.serval.util.distance;

/**
 * Created by thames1990 on 01.09.17.
 */
public class Distance {
    
    private double value;
    
    private DistanceUnit unit;
    
    public Distance(double value, DistanceUnit unit) {
        this.value = value;
        this.unit = unit;
    }
    
    public Distance toUnit(DistanceUnit unit) {
        double newValue = unit.toDistance(value, unit);
        return new Distance(newValue, unit);
    }
    
    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
