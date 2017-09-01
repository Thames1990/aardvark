package de.uni_marburg.mathematik.ds.serval.util;

import de.uni_marburg.mathematik.ds.serval.R;

/**
 * Created by thames1990 on 01.09.17.
 */
public class DataTypeConversionUtil {
    
    public static int safeLongToInt(long value) {
        if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
            throw new IllegalArgumentException(String.format(
                    String.valueOf(R.string.exception_cannot_cast),
                    value
            ));
        }
        return (int) value;
    }
}
