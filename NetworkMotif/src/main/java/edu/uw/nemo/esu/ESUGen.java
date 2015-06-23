package edu.uw.nemo.esu;

import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.model.NeighbourhoodCache;
import edu.uw.nemo.model.SubGraph;
import edu.uw.nemo.labeler.GraphLabel;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Generate motifs of given length with ESU algorithm
 * @author joglekaa
 * @author vartikav
 */
public class ESUGen {
    private NeighbourhoodCache cachedNeighborhoods;
    
    /**
     * Construct the ESUGen instance.
     * @param enableCache If true, a neighbourhood cache will be used while enumerating subgraphs.
     */
    public ESUGen(boolean enableCache) {
        this.cachedNeighborhoods = new NeighbourhoodCache(false);
    }

    /**
     * Enumerate all the sub-graphs from the original graph which is represented by the {@code mapping} instance.
     * @param mapping The in-memory representation of the original graph for which motifs need to be enumerated.
     * @param label The instance of {@link GraphLabel} to hold the enumerated subgraphs after converting to g6 format.
     * @param length The size of the subgraphs to enumerate.
     */
    public void enumerateSubgraphs(Mapping mapping, GraphLabel label, int length) {
        // time start
        long start = System.currentTimeMillis();
        // get all nodes
        List<Integer> nodes = mapping.getIds();
        // initialize a subgraph of given length
        SubGraph subGraph = new SubGraph(length, false);
        // initialize the extension array which will be used to extend the subgraph later
        List<Integer> extension = new ArrayList<Integer>();
        for (Integer node : nodes) {
            // clear the sub graph
            subGraph.clear();
            // add the node as the first vertex into the subgraph
            subGraph.add(node);
            // add all the neighbours of the first node with id greater than the node itself to the extension
            filterGreater(extension, mapping.getNeighbours(node), node);
            // extend subgraph recursively untill required lenght is meet
            extendSubgraph(mapping, subGraph, extension, 0, extension.size(), length, label);
        }
        // time stop 
        System.out.println("Enumerating subgraph took " + (System.currentTimeMillis() - start) + " milliseconds.");
    }

    /**
     * Enumerate sample sub-graphs from the original graph which is represented by the {@code mapping} instance.
     * @param mapping : Ranodm graph mapping object
     * @param label :The instance of {@link GraphLabel} to hold the enumerated subgraphs after converting to g6 format.
     * @param length : The size of the subgraphs to enumerate.
     * @param probability: how many vetices
     */
    public void enumerateSubgraphs(Mapping mapping, GraphLabel label, int length, double probability) {

        // time start
        long start = System.currentTimeMillis();

        // get all nodes
       // List<Integer> nodes = mapping.getIds();
        List<Integer> nodes = getSampleNodes(mapping.getNodeCount(), probability);
        // initialize a subgraph of given length
        SubGraph subGraph = new SubGraph(length, false);
        // initialize the extension array which will be used to extend the subgraph later
        List<Integer> extension = new ArrayList<Integer>();
        for (Integer node : nodes) {
            // clear the sub graph
            subGraph.clear();
            // add the node as the first vertex into the subgraph
            subGraph.add(node);
            // add all the neighbours of the first node with id greater than the node itself to the extension
            filterGreater(extension, mapping.getNeighbours(node), node);
            // extend subgraph recursively untill required lenght is meet
            extendSubgraph(mapping, subGraph, extension, 0, extension.size(), length, label);
        }
        // time stop
        System.out.println("Enumerating subgraph took " + (System.currentTimeMillis() - start) + " milliseconds.");
    }

    // Filter those vertices from the {@code neighbours} which have id smaller than the {@code node} itself. Add rest to the {@code extension}.
    private void filterGreater(List<Integer> extension, List<AdjacentVertexWithEdge> neighbours, Integer node) {
        extension.clear();
        for(AdjacentVertexWithEdge i : neighbours) {
            if (i.getNodeId() > node) {
                extension.add(i.getNodeId());
            }
        }
    }

    /**
     * Generating list of sample nodes based on probability
     * @param totalNodes
     * @param prob
     * @return
     */
    List<Integer> getSampleNodes(int totalNodes, double prob)
    {
        List<Integer> nodeList = new ArrayList<Integer>();
        //System.out.println(totalNodes);
        if(totalNodes == 0)
        {
            nodeList.add(-1);
            return nodeList;
        }
        boolean sel[] = new boolean[totalNodes];
        double rem = (double)totalNodes * prob;
        int n = (int)rem;
        n =n + 1;

        if(prob > 0.0 && prob < 0.6) //case of small probabilities
        {
          //  System.out.println(n);
            while(n != 0)
            {
               // System.out.println(n);
                int pos = randomNumberGenerator(totalNodes);
                if(!sel[pos])
                {
                    sel[pos] = true;
                    nodeList.add(pos);
                    --n;
                }
            }
        }
        //If probability is large then number of new nodes will also be large.
        //So in order to avoid that many number of random generation following code
        //set the selected array true for those many number of nodes which are not going to get selected.
        //so random number generates number which are are not going get selected. Those number will be less.
        else //case of large probabilities
        {
            if(prob == 1.0)
            {
                n = totalNodes;
            }
            else {
                n = totalNodes - n;
            }
            while(n != 0)
            {
                int pos = randomNumberGenerator(totalNodes);
                if(!sel[pos])
                {
                    sel[pos] = true;
                    --n;
                }
            }
            for(int i = 0; i< totalNodes; i++)
            {
                if(!sel[i])
                {
                    nodeList.add(i);
                }
            }
        }
        return nodeList;
    }

    /**
     * Random object
     * @return
     */
    public int randomNumberGenerator(int n)
    {
        long seed =  System.currentTimeMillis();
        Random rand = new Random(seed);
        return rand.nextInt(n);
    }

    // extend subgraph recursively untill required lenght is meet
    private void extendSubgraph(
            Mapping mapping,
            SubGraph subGraph,
            List<Integer> extension,
            int extensionBegin,
            int extensionEnd,
            int motifLength,
            GraphLabel label) {
        if (subGraph.size() == motifLength) {
            // The subgraph size meets the motif length requirement, add it to the set of subgraphs enumerated.
            label.addSubGraph(mapping, subGraph.getVertices());
            if (label.getSubgraphCount() % 50000 == 0) {
                // Print progress after every 50,000 subgraphs enumerated
                System.out.println("Enumerated " + label.getSubgraphCount() + " sub-graphs till now.");
            }
        } else {
            Integer root = subGraph.get(0);
            // continue untill the whole extension is consumed
            while(extensionBegin < extensionEnd) {
                // get the next vertex from the extension
                int vertex = extension.get(extensionBegin++);
                // prepare the exclusive neighbourhood that will be required for extending extension
                ExclusiveNeighbourhood exN = new ExclusiveNeighbourhood(mapping, this.cachedNeighborhoods, subGraph, root);
                // extend the extension will newly found neighbours in the exclusive neighbourhood
                int newExtensionSize = extendExtension(extension, extensionEnd, exN, mapping.getNeighbours(vertex));
                // add the vertex to the subgraph and recursively call extendSubgraph
                subGraph.add(vertex);
                extendSubgraph(mapping, subGraph, extension, extensionBegin, newExtensionSize, motifLength, label);
                // at this point, all the subgraphs with the vertex are enumerated. Hence remove it now.
                subGraph.remove();
            }
        }
    }

    // extend the extension will newly found neighbours in the exclusive neighbourhood
    private int extendExtension(
            List<Integer> extension,
            int currentEnd,
            ExclusiveNeighbourhood exN,
            List<AdjacentVertexWithEdge> neighbourhood) {
        int targetIndex = currentEnd;
        for (AdjacentVertexWithEdge v : neighbourhood) {
            if (!exN.isInExclusiveNeighbourhood(v)) {
                continue;
            }
            
            if (targetIndex < extension.size()) {
                extension.set(targetIndex, v.getNodeId());
            }
            else
            {
                extension.add(v.getNodeId());
            }
            
            targetIndex++;
        }
        
        return targetIndex;
    }

    /**
     * A class to help compute exclusive neighbourhood of a subgraph
     */
    private class ExclusiveNeighbourhood {
        private final SubGraph subGraph;
        private final int root;
        private List<AdjacentVertexWithEdge> subGraphNeighbourhood;
        
        /**
         * Construct the {@link ExclusiveNeighbourhood} instance.
         * @param mapping The in-memory representation of the original graph
         * @param cachedNeighborhoods The cache of neighbourhoods
         * @param subGraph The subgraph for which exclusive neighbourhood need to be maintained.
         * @param root The root of the subgraph (or its first vertex)
         */
        public ExclusiveNeighbourhood(
                Mapping mapping,
                NeighbourhoodCache cachedNeighborhoods,
                SubGraph subGraph,
                int root) {
            this.subGraph = subGraph;
            this.root = root;
            
            subGraphNeighbourhood = cachedNeighborhoods.getNeighbourhood(subGraph);
            if (subGraphNeighbourhood == null) {
                subGraphNeighbourhood = new ArrayList<AdjacentVertexWithEdge>();
                for (int i = 0; i < subGraph.size(); i++) {
                    subGraphNeighbourhood.addAll(mapping.getNeighbours(subGraph.get(i)));
                }

                cachedNeighborhoods.addNeighbourhood(subGraph, subGraphNeighbourhood);
            }
        }
        
        /**
         * Check whether the given vertex: {@code v} is in the exclusive neighbourhood.
         * @param v The vertex to check
         * @return True, if the vertex is in exclusive neighbourhood of the subgraph. False otherwise.
         */
        public boolean isInExclusiveNeighbourhood(AdjacentVertexWithEdge v) {
            if (v.getNodeId() <= this.root) {
                return false;
            }
            
            if (this.subGraphNeighbourhood.contains(v) || this.subGraph.contains(v.getNodeId())) {
                return false;
            }
            
            return true;
        }
    }
}
