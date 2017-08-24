package de.uni_marburg.mathematik.ds.serval.model;

import java.util.List;

/**
 * Created by thames1990 on 22.08.17.
 */

public class TestItem extends Item {

    static final long serialVersionUID = 5L;

    public TestItem(long time, List<Measurement> measurements, Location location) {
        super(time, measurements, location);
    }
}
