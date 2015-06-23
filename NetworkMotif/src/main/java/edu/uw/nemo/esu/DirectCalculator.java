package edu.uw.nemo.esu;

import edu.uw.nemo.labeler.GraphFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by joglekaa on 5/30/14.
 */
public class DirectCalculator {
    public Map<String, List<Integer>> standardConcentrations(Map<String, List<GraphFormat>> canonicalSubgraphs) {
        HashMap<String, List<Integer>> result = new HashMap<String, List<Integer>>();
        for (String label : canonicalSubgraphs.keySet()) {
            // call the equivalent method for getting standard concentrations
            // store the output in result
        }
        return result;
    }
}
