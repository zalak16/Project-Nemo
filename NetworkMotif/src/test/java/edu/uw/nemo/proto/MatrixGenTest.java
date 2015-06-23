package edu.uw.nemo.proto;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Created by joglekaa on 5/14/14.
 */
public class MatrixGenTest {

    private final GenUtil genUtil = new GenUtil();

    @Test public void validateGraphConversion() {

        MatrixGen target = new MatrixGen();
        byte[] actual = target.graphToAdjacencyMatrix(genUtil.setupAL(), new int[]{1, 2, 3});

        byte[] expected = new byte[] {0, 1, 1, 1, 0, 1, 1, 1, 0 };

        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i]);
        }
    }

}
