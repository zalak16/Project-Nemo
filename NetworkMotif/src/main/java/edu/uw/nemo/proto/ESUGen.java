package edu.uw.nemo.proto;

import java.util.*;

/**
 * Created by joglekaa on 4/28/14.
 */
public class ESUGen {

    public List<int[]> enumerateSubgraphs(List<int[]> adj, int length) {
        ArrayList<int[]> result = new ArrayList<int[]>();
        List<Integer> nodes = startingNodes(adj);
        for (Integer node : nodes) {
            List<Integer> subGraph = new ArrayList<Integer>();
            subGraph.add(node);
            List<Integer> extension = filterGreater(getNeighbours(adj, node), node);
            extendSubgraph(adj, subGraph, extension, length, result);
        }
        return result;
    }

    private List<Integer> filterGreater(List<Integer> neighbours, Integer node) {
        ArrayList<Integer> filetered = new ArrayList<Integer>();
        for(int i : neighbours) {
            if (i > node) {
                filetered.add(i);
            }
        }
        return filetered;
    }

    private void extendSubgraph(List<int[]> adjacencyList, List<Integer> subGraph, List<Integer> extension, int motifLength, List<int[]> collector) {
        int size = subGraph.size();
        if (size >= motifLength) {
            int[] x = new int[motifLength];
            for (int i = 0; i < motifLength; i++) {
                x[i] = subGraph.get(i);
            }
            collector.add(x);
        } else {
//            System.out.print("sub graph = "); print(subGraph); System.out.print(", ");
//            System.out.print("extension = "); print(extension); System.out.println("");
            Integer root = subGraph.get(0);
            while(extension.size() > 0) {
                int vertex = extension.get(0);
                extension.remove(0);
                Set<Integer> nExt = new HashSet<Integer>(extension);
                nExt.addAll(filterGreater(getExclusiveNeighbours(adjacencyList, subGraph, vertex), root));
                nExt.remove(vertex);
                subGraph.add(vertex);
                extendSubgraph(adjacencyList, subGraph, new ArrayList<Integer>(nExt), motifLength, collector);
                subGraph.remove(subGraph.size() - 1);
            }
        }
    }

    private List<Integer> startingNodes(List<int[]> adj) {
        Set<Integer> result = new HashSet<Integer>();
        for (int[] cur : adj) {
            result.add(cur[0]);
            result.add(cur[1]);
        }
        return new ArrayList<Integer>(result);
    }

    private List<Integer> getExclusiveNeighbours(List<int[]> adjList, List<Integer> subGraph, int node) {
        Set<Integer> old = new HashSet<Integer>();
        for (Integer x : subGraph) {
            old.addAll(getNeighbours(adjList, x));
        }
        Set<Integer> n = new HashSet<Integer>(getNeighbours(adjList, node));
        n.removeAll(old);
        return new ArrayList<Integer>(n);
    }

    private List<Integer> getNeighbours(List<int[]> adjList, int node) {
        HashSet<Integer> neighbours = new HashSet<Integer>();
        for (int[] link : adjList) {
            if (node == link[0]) {
                neighbours.add(link[1]);
            } else if (node == link[1]) {
                neighbours.add(link[0]);
            }
        }

        return new ArrayList<Integer>(neighbours);
    }

    void print(List<Integer> input) {
        for(int i : input) {
            System.out.print(i + " ");
        }
//        System.out.println("");
    }

}
