package edu.uw.nemo.model;

import java.util.*;

/**
 * This class holds an in-memory structure of a graph.
 * @author joglekaa
 * @author vartikav
 */
public class Mapping {

    private final List<String[]> edgesList;

    private final AdjacencyMapping adjMapping;


    public AdjacencyMapping getAdjMapping() {
        return adjMapping;
    }


    /**
     * Constructs the {@link Mapping} object
     * @param edgesList The list of edges in the graph.
     * @param adjMapping The adjacency mapping representation of the graph.
     */
    public Mapping(List<String[]> edgesList, AdjacencyMapping adjMapping) {
        this.edgesList = edgesList;
        this.adjMapping = adjMapping;
    }

    /**
     * Get the number of nodes in the graph.
     * @return The number of nodes in graph.
     */
    public int getNodeCount() {
        return (adjMapping != null) ? adjMapping.size() : 0;
    }

    /**
     * Get the number of edges in the graph.
     * @return The number of edges in graph.
     */
    public int getLinkCount() {
        return edgesList != null ? edgesList.size() : 0;
    }

    /**
     * Get the number of edges in the graph from adjacency mapping
     * @return The number of edges
     */
    public int getTotalEdges(){
        if(adjMapping != null)
        {
            int edgeCount = 0;
            for(int i= 0; i< getNodeCount(); i++)
            {
                edgeCount += adjMapping.getNeighbours(i).size();
            }
            return (edgeCount/2);
        }
        return 0;
    }

    /**
     * Get list of all the ids that this mapping hold.
     * @return List of ids
     */
    public List<Integer> getIds() {
        List<Integer> ids = new ArrayList<Integer>();
        for (int i = 0; i < adjMapping.size(); i++) {
            ids.add(i);
        }
        
        return ids;
    }

    /**
     * Returns the list of nodes for the given node which are adjacent to it.
     * @param node The id of the node for which neighbours need to be looked up.
     * @return The list of adjacent nodes each represented by {@link AdjacentVertexWithEdge}.
     */
    public List<AdjacentVertexWithEdge> getNeighbours(Integer node) {
        return this.adjMapping.getNeighbours(node);
    }
    
    /**
     * Checks whether the given adjNodeId is a neighbour of given nodeId.
     * If yes, then returns the {@link AdjacentVertexWithEdge} representation of the adjNodeId, otherwise null.
     * @param nodeId The node for which adjacency list need to be looked up.
     * @param adjNodeId The node which need to be looked up in the adjacency list.
     * @return The {@link AdjacentVertexWithEdge} representation of adjNodeId if found, otherwise false.
     */
    public AdjacentVertexWithEdge getNeighbour(int nodeId, int adjNodeId) {
        return this.adjMapping.getNeighbour(nodeId, adjNodeId);
    }
}
