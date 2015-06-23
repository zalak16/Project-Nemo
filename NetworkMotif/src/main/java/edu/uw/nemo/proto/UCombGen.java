package edu.uw.nemo.proto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by joglekaa on 4/16/14.
 */
public class UCombGen {

    List<int[]> enumerateSubgraphs(List<int[]> adj, int length) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        List<Integer> nodes = startingNodes(adj);
        for (Integer node : nodes) {
            List<Integer> comb = new ArrayList<Integer>();
            comb.add(node);
            HashSet<Integer> touched = new HashSet<Integer>();
            extendSubgraph(adj, comb, length, touched, result);
        }
        return result;
    }

    private void extendSubgraph(List<int[]> adjacencyList, List<Integer> combination, int motifLength, Set<Integer> touched, List<int[]> collector) {
        int size = combination.size();
        if (size >= motifLength) {
            int[] x = new int[motifLength];
            for (int i = 0; i < motifLength; i++) {
                x[i] = combination.get(i);
            }
            collector.add(x);
        } else {
            foo(adjacencyList, combination, motifLength, touched, collector);
            if (size > 1) {
                foo(adjacencyList, reverse(combination), motifLength, touched, collector);
            }
        }
    }

    private List<Integer> reverse(List<Integer> combination) {
        ArrayList<Integer> rev = new ArrayList<Integer>(combination.size());
        for (int index = combination.size() - 1; index > 0; index--) {
            rev.add(combination.get(index));
        }
        return rev;

    }

    private void foo(List<int[]> adjacencyList, List<Integer> combination, int motifLength, Set<Integer> touched, List<int[]> collector) {
        int size = combination.size();
        if (size < 1) return;
        int current = combination.get(size - 1);
        int starter = combination.get(0);
        touched.add(current);
        List<Integer> linked = linkedNodes(adjacencyList, current, starter, touched);
        HashSet<Integer> augmented = new HashSet<Integer>(touched);
//        augmented.addAll(linked);
        for (int link : linked) {
            combination.add(size, link);
//            augmented.remove(link);
            extendSubgraph(adjacencyList, combination, motifLength, augmented, collector);
//            augmented.add(link);
            combination.remove(size);
        }
    }

    private List<Integer> linkedNodes(List<int[]> links, int current, int starter, Set<Integer> touched) {
        ArrayList<Integer> linked = new ArrayList<Integer>();
        for (int[] link : links) {
            if (current == link[0] && !touched.contains(link[1]) && link[1] > starter) {
                linked.add(link[1]);
            } else if (current == link[1] && !touched.contains(link[0]) && link[0] > starter) {
                linked.add(link[0]);
            }
        }
        return linked;
    }

    private List<Integer> startingNodes(List<int[]> adj) {
        Set<Integer> result = new HashSet<Integer>();
        for (int[] cur : adj) {
            result.add(cur[0]);
            result.add(cur[1]);
        }
        return new ArrayList<Integer>(result);
    }

}
