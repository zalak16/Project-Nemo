package edu.uw.nemo.esu;

import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.labeler.GraphLabel;
import org.junit.Test;

import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class ESUGenTest {

    private final GenUtil genUtil = new GenUtil();

    @Test
    public void assertDefaultGen() {
        Mapping input = genUtil.buildMapping(genUtil.setupAL());

        ESUGen target = new ESUGen(false);
        GraphLabel label = new GraphLabel(false);
        target.enumerateSubgraphs(input, label, 2);
//        genUtil.printCombinations(actual);
        assertEquals(9, label.getSubgraphCount());

        label.clear();
        target.enumerateSubgraphs(input, label, 3);
        assertEquals(16, label.getSubgraphCount());
        //genUtil.printCombinationsOfVertices(actual2);

        label.clear();
        target.enumerateSubgraphs(input, label, 4);
        assertEquals(24, label.getSubgraphCount());
//        genUtil.printCombinations(actual3);
    }

}