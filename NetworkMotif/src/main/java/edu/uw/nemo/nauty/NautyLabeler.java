package edu.uw.nemo.nauty;

import edu.uw.nemo.labeler.GraphLabel;
import edu.uw.nemo.model.Mapping;

import java.util.*;

/**
 * converts a list of subgraphs to map of canonical labels to equivalent subgraphs
 * Created by anand joglekar on 5/30/14.
 */
public class NautyLabeler {
    public Map<String, List<Map.Entry<String, Long>>> mapCanonical(Mapping mapping, List<int[]> subgraphs, int size) {
        long start = System.currentTimeMillis();
        GraphLabel label = new GraphLabel(true);
        if (subgraphs != null) {
            for (int[] subGraph : subgraphs) {
                if (subGraph == null) {
                    continue;
                }
                label.addSubGraph(mapping, subGraph);
            }
        }
        
        System.out.println("Loading subgraphs for canonical labels took " + (System.currentTimeMillis() - start) + " milliseconds.");

        start = System.currentTimeMillis();
        Map<String, List<Map.Entry<String, Long>>> canonicalLabels = label.getCanonicalLabels();
        System.out.println("Generating canonical labels took " + (System.currentTimeMillis() - start) + " milliseconds.");
        return canonicalLabels;
    }
}
