package edu.uw.nemo.proto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by joglekaa on 4/28/14.
 */
public class ESUPGen {

    public List<int[]> enumerateSubgraphs(List<int[]> adj, int length) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        List<Integer> nodes = startingNodes(adj);
        for (Integer node : nodes) {
            List<Integer> comb = new ArrayList<Integer>();
            comb.add(node);
            extendSubgraph(adj, comb, length, result);
        }
        return result;
    }

    private void extendSubgraph(List<int[]> adjacencyList, List<Integer> combination, int motifLength, List<int[]> collector) {
        int size = combination.size();
        if (size >= motifLength) {
            int[] x = new int[motifLength];
            for (int i = 0; i < motifLength; i++) {
                x[i] = combination.get(i);
            }
            collector.add(x);
        } else {
            esu(adjacencyList, combination, motifLength, collector);
            if (size > 1) {
                esu(adjacencyList, reverse(combination), motifLength, collector);
            }
        }
    }

    private List<Integer> reverse(List<Integer> combination) {
        ArrayList<Integer> rev = new ArrayList<Integer>(combination.size());
        for (int index = combination.size() - 1; index >= 0; index--) {
            rev.add(combination.get(index));
        }
        return rev;
    }

    private void esu(List<int[]> adjacencyList, List<Integer> combination, int motifLength, List<int[]> collector) {
        int size = combination.size();
        if (size < 1) return;
        int current = combination.get(size - 1);
        int starter = combination.get(0);
        HashSet<int[]> neighbours = getPaths(adjacencyList, combination, size);
        List<Integer> linked = linkedNodes(adjacencyList, current, starter, neighbours);
        for (int link : linked) {
            combination.add(size, link);
            extendSubgraph(adjacencyList, combination, motifLength, collector);
            combination.remove(size);
        }
    }

    private HashSet<int[]> getPaths(List<int[]> adjacencyList, List<Integer> combination, int size) {
        HashSet<int[]> neighbours = new HashSet<int[]>();
        for(int i = 0; i < size - 1; i++) {
            Integer current = combination.get(i);
            for (int[] x : adjacencyList) {
                if (x[0] == current) {
                    neighbours.add(x);
                } else if (x[1] == current) {
                    neighbours.add(x);
                }
            }
        }
        return neighbours;
    }

    private List<Integer> linkedNodes(List<int[]> links, int current, int starter, Set<int[]> neighbours) {
        ArrayList<Integer> linked = new ArrayList<Integer>();
        for (int[] link : links) {
            if (current == link[0] && !neighbours.contains(link) && link[1] > starter) {
                linked.add(link[1]);
            } else if (current == link[1] && !neighbours.contains(link) && link[0] > starter) {
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
