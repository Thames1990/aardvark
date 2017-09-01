package de.uni_marburg.mathematik.ds.serval.util.distance;

/**
 * Created by thames1990 on 01.09.17.
 */
public enum DistanceUnit {
    
    KILOMETER {
        @Override
        protected double conversionFactor(DistanceUnit toDistanceUnit) {
            switch (toDistanceUnit) {
                case KILOMETER:
                    return 1;
                case MILE:
                    return 0.621371;
                default:
                    throw new UnsupportedOperationException(toDistanceUnit + " is not supported");
            }
        }
    },
    MILE {
        @Override
        protected double conversionFactor(DistanceUnit toDistanceUnit) {
            switch (toDistanceUnit) {
                case KILOMETER:
                    return 1.60934;
                case MILE:
                    return 1;
                default:
                    throw new UnsupportedOperationException(toDistanceUnit + " is not supported");
            }
        }
    };
    
    public double toDistance(double value, DistanceUnit targetDistance) {
        return value * conversionFactor(targetDistance);
    }
    
    protected abstract double conversionFactor(DistanceUnit toDistanceUnit);
}
