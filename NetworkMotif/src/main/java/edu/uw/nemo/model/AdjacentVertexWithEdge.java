/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.model;

/**
 * This class holds the id of an adjacent node and the edge corresponding to the adjacent vertices.
 * This class facilitates readily available edge to avoid GC overhead if edges are created repeatedly and discarded.
 * @author vartikav
 */
public class AdjacentVertexWithEdge {
    private final int nodeId;
    private final int[] edge;

    /**
     * Constructs a {@link AdjacentVertexWithEdge}
     * @param nodeId The id for this adjacent node.
     * @param sourceNodeId The id of the source node, to which this node is adjacent.
     */
    public AdjacentVertexWithEdge(int nodeId, int sourceNodeId) {
        this.nodeId = nodeId;
        this.edge = new int[2];
        this.edge[0] = sourceNodeId;
        this.edge[1] = this.nodeId;
    }

    /**
     * Returns the id of the node.
     * @return This nodes id.
     */
    public int getNodeId() {
        return this.nodeId;
    }

    /**
     * Returns the edge between this node and the source node to which it is adjacent.
     * @return A pair of integers which represent the edge [sourceNodeId, nodeId]
     */
    public int[] getEdge() {
        return this.edge;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof AdjacentVertexWithEdge)) {
            return false;
        }
        
        if (obj == this) {
            return true;
        }
        
        AdjacentVertexWithEdge objAsType = (AdjacentVertexWithEdge)obj;
        return objAsType.nodeId == this.nodeId;
    }
    
    @Override
    public int hashCode() {
        return this.nodeId;
    }
}
