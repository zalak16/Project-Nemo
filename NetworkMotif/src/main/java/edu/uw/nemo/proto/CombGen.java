package edu.uw.nemo.proto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by joglekaa on 4/14/14.
 */
public class CombGen {

    List<int[]> generate(List<int[]> adj, int length) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        List<Integer> nodes = startingNodes(adj);
        for (Integer node : nodes) {
            List<Integer> comb = new ArrayList<Integer>();
            comb.add(node);
            gen(adj, comb, length, result);
        }
        return result;
    }

    private void gen(List<int[]> adjacencyList, List<Integer> combination, int motifLength, ArrayList<int[]> collector) {
        int size = combination.size();
        if (size >= motifLength) {
            int[] x = new int[motifLength];
            for (int i = 0; i < motifLength; i++) {
                x[i] = combination.get(i);
            }
            collector.add(x);
        } else {
            int current = combination.get(size - 1);
            List<Integer> linked = linkedNodes(adjacencyList, current);
            for (int link : linked) {
                combination.add(size, link);
                gen(adjacencyList, combination, motifLength, collector);
                combination.remove(size);
            }
        }
    }

    private List<Integer> linkedNodes(List<int[]> links, int current) {
        ArrayList<Integer> linked = new ArrayList<Integer>();
        for (int[] link : links) {
            if (current == link[0]) {
                linked.add(link[1]);
            }
        }
        return linked;
    }

    private List<Integer> startingNodes(List<int[]> adj) {
        Set<Integer> result = new HashSet<Integer>();
        for (int[] cur : adj) {
            result.add(cur[0]);
        }
        return new ArrayList<Integer>(result);
    }

}

