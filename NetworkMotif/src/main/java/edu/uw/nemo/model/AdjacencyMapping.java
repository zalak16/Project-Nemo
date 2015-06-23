/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.model;

import java.util.ArrayList;
import java.util.List;

/**
 * This class hold the adjacency map representation of a graph 
 * @author vartikav
 */
public class AdjacencyMapping {
    
    private List<List<AdjacentVertexWithEdge>> adjacencyList;
    
    /**
     * Constructs the AdjacencyMapping object which will have given number of nodes.
     * @param capacity The number of nodes that this adjacency map will have.
     */
    public AdjacencyMapping(int capacity) {
        this.adjacencyList = new ArrayList<List<AdjacentVertexWithEdge>>(capacity);
    }
    
    /**
     * Returns the size of the adjacency mapping.
     * @return The number of nodes in it.
     */
    public int size() {
        return this.adjacencyList.size();
    }
    
    /**
     * Returns the list of nodes for the given node which are adjacent to it.
     * @param nodeId The id of the node for which neighbours need to be looked up.
     * @return The list of adjacent nodes each represented by {@link AdjacentVertexWithEdge}.
     */
    public List<AdjacentVertexWithEdge> getNeighbours(int nodeId) {
        return this.adjacencyList.get(nodeId);
    }
    
    /**
     * Checks whether the given adjNodeId is a neighbour of given nodeId.
     * If yes, then returns the {@link AdjacentVertexWithEdge} representation of the adjNodeId, otherwise null.
     * @param nodeId The node for which adjacency list need to be looked up.
     * @param adjNodeId The node which need to be looked up in the adjacency list.
     * @return The {@link AdjacentVertexWithEdge} representation of adjNodeId if found, otherwise false.
     */
    public AdjacentVertexWithEdge getNeighbour(int nodeId, int adjNodeId) {
        List<AdjacentVertexWithEdge> neighbours = this.getNeighbours(nodeId);
        for (AdjacentVertexWithEdge neighbour : neighbours) {
            if (neighbour.getNodeId() == adjNodeId) {
                return neighbour;
            }
        }
        
        return null;
    }
    
    /**
     * Adds the pair of adjacent nodes to the adjacency mapping.
     * @param nodeId1 The first node.
     * @param nodeId2 The second node.
     */
    public void addAdjacentVertices(int nodeId1, int nodeId2) {
        this.ensureSize(nodeId1 > nodeId2 ? nodeId1 : nodeId2);
        
        List<AdjacentVertexWithEdge> node1AdjList = this.adjacencyList.get(nodeId1);
        node1AdjList.add(new AdjacentVertexWithEdge(nodeId2, nodeId1));
        
        List<AdjacentVertexWithEdge> node2AdjList = this.adjacencyList.get(nodeId2);
        node2AdjList.add(new AdjacentVertexWithEdge(nodeId1, nodeId2));
    }
    
    private void ensureSize(int index) {
        if (this.adjacencyList.size() > index) {
            return;
        }

        for (int i = this.adjacencyList.size(); i < index + 1; i++) {
            this.adjacencyList.add(new ArrayList<AdjacentVertexWithEdge>());
        }
    }
}
