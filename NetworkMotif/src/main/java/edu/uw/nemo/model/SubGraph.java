/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.model;

import java.util.Arrays;
import java.util.Stack;

/**
 * The class to hold a subgraph which can grow or shrink effectively during runtime
 * to avoid high GC if the subgraph is created from scratch each time.
 * @author vartikav
 */
public class SubGraph {
    
    private final int[] vertices;
    private int vertexCount;
    private boolean keepSorted;
    private Stack<Integer> lastVertices;

    /**
     * Constructs the subgraph with provided maximum number of vertices.
     * At this stage, the subgraph is empty with no vertex in it.
     * @param size The maximum number of vertices that this subgraph can have.
     * @param keepSorted If true, will keep vertices in sorted order
     */
    public SubGraph(int size, boolean keepSorted) {
        this.vertices = new int[size];
        this.vertexCount = 0;
        this.keepSorted = keepSorted;
        this.lastVertices = new Stack<Integer>();
    }

    /**
     * Get the list of vertices in this subgraph.
     * @return The list of vertices.
     */
    public int[] getVertices() {
        return this.vertices;
    }
    
    /**
     * Get the list of vertices in this subgraph in sorted order.
     * @return The list of vertices in sorted order.
     */
    public int[] getSortedVertices() {
        if (this.keepSorted) {
            return this.vertices;
        }
        else {
            int[] verticesCopy = Arrays.copyOf(this.vertices, this.vertexCount);
            Arrays.sort(verticesCopy);
            return verticesCopy;
        }
    }

    /**
     * Add a vertex to this subgraph.
     * @param vertex The vertex to add.
     */
    public void add(int vertex) {
        if (!this.keepSorted) {
            this.vertices[this.vertexCount++] = vertex;
        }
        else {
            int insertPos = this.vertexCount;
            while (insertPos > 0 && this.vertices[insertPos - 1] > vertex) {
                this.vertices[insertPos] = this.vertices[insertPos - 1];
                insertPos--;
            }
            
            this.vertices[insertPos] = vertex;
            this.vertexCount++;
            this.lastVertices.push(insertPos);
        }
    }

    /**
     * Remove the last vertex from this subgraph.
     */
    public void remove() {
        if (!this.keepSorted) {
            this.vertexCount--;
        }
        else {
            int vertexToRemove = this.lastVertices.pop();
            for (int i = vertexToRemove; i < this.vertexCount - 1; i++) {
                this.vertices[i] = this.vertices[i + 1];
            }
            
            this.vertexCount--;
        }
    }

    /**
     * Clear the subgraph by setting the vertex count to zero.
     */
    public void clear() {
        this.vertexCount = 0;
        if (this.keepSorted) {
            this.lastVertices.clear();
        }
    }

    /**
     * Get the size of the subgraph.
     * @return The number of vertices in this subgraph.
     */
    public int size() {
        return this.vertexCount;
    }

    /**
     * Get the id of the vertex at the given index in this subgraph.
     * @param index The index into vertex list of the subgraph.
     * @return The id of the vertex at the given index.
     */
    public int get(int index) {
        return this.vertices[index];
    }

    /**
     * CHecks whether the vertex with given id is in the subgraph.
     * @param vertex The id of the vertex to search.
     * @return True, if vertex is in subgraph.
     */
    public boolean contains(int vertex) {
        for (int i = 0; i < this.vertexCount; i++) {
            if (this.vertices[i] == vertex) {
                return true;
            }
        }

        return false;
    }
    
    /**
     * Returns the comma separated list of vertices in the graph in sorted order
     * @return comma separated list of vertices in sorted order
     */
    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        if (!this.keepSorted) {
            int lastVertex = -1;
            for (int i = 0; i < this.vertexCount; i++) {
                int nextVertex = this.vertices[0];
                for (int j = 1; j < this.vertexCount; j++) {
                    if (nextVertex <= lastVertex ||
                        (this.vertices[j] < nextVertex && this.vertices[j] > lastVertex)) {
                        nextVertex = this.vertices[j];
                    }
                }

                lastVertex = nextVertex;
                str.append(nextVertex);
                str.append(',');
            }
        }
        else {
            for (int i = 0; i < this.vertexCount; i++) {
                str.append(this.vertices[i]);
                str.append(',');
            }
        }
        
        return str.substring(0, str.length() - 1);
    }
}
