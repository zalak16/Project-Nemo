package edu.uw.nemo.proto;

import org.junit.Test;

import java.util.List;

/**
 * Created by joglekaa on 4/16/14.
 */
public class ESUPGenTest {

    private final GenUtil genUtil = new GenUtil();

    @Test public void assertDefaultGen() {
        List<int[]> input = genUtil.setupAL();

        ESUPGen target = new ESUPGen();

        List<int[]> actual2 = target.enumerateSubgraphs(input, 3);
        genUtil.printCombinations(actual2);
    }

}
