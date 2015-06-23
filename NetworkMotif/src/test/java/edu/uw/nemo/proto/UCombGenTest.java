package edu.uw.nemo.proto;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by joglekaa on 4/16/14.
 */
public class UCombGenTest {

    private final GenUtil genUtil = new GenUtil();

    @Test public void assertDefaultGen() {
        List<int[]> input = genUtil.setupAL();

        UCombGen target = new UCombGen();
//        List<int[]> actual = target.enumerateSubgraphs(input, 2);
//        genUtil.printCombinations(actual);
//        assertEquals(9, actual.size());

        List<int[]> actual2 = target.enumerateSubgraphs(input, 3);
        genUtil.printCombinations(actual2);
    }

}
