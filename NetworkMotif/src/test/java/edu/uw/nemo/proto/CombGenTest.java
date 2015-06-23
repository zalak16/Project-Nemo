package edu.uw.nemo.proto;

import org.junit.Test;

import java.util.List;

import static junit.framework.Assert.assertEquals;

/**
 * Created by joglekaa on 4/14/14.
 */
public class CombGenTest {

    private final GenUtil genUtil = new GenUtil();

    @Test public void assertGenerationOfNonDupCombinations() {

        List<int[]> input = genUtil.setupAL();

        CombGen target = new CombGen();
        List<int[]> actual = target.generate(input, 2);
        genUtil.printCombinations(actual);
        assertEquals(9, actual.size());

        List<int[]> actual2 = target.generate(input, 3);
        genUtil.printCombinations(actual2);
    }

}
