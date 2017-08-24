package de.uni_marburg.mathematik.ds.serval;

import org.junit.Test;

import static de.uni_marburg.mathematik.ds.serval.model.util.GeohashUtil.decodeGeohash;
import static de.uni_marburg.mathematik.ds.serval.model.util.GeohashUtil.encodeGeohash;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Created by thames1990 on 21.08.17.
 */
public class GeohashUtilTest {

    @Test
    public void testEncodeGeohash() {
        assertEquals("u1n3098dzvkx", encodeGeohash(50.80952253171202, 8.81206054593121, 12));
    }

    @Test
    public void testDecodeGeohash() {
        assertArrayEquals(
                new double[]{50.80952253171202, 8.81206054593121},
                decodeGeohash("u1n3098dzvkx"),
                0.000001
        );
    }
}
