/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.labeler;

import java.nio.charset.Charset;
import java.util.*;

import edu.uw.nemo.model.*;

/**
 * Formats the graph into format used by nauty library for canonical labeling.
 * Supports two formats used in nauty - graph6 and sparse6.
 * For more information about graph formats, refer src/main/nauty/nauty25r9/formats.txt
 * @author vartikav
 */
public class GraphFormat {
    
    private FormatType formatType;
    private List<int[]> edges;
    private int vertexCount;
    private Mapping mapping;
    private int[] vertices;
    private boolean useNodeIndexer;
    
    private byte[] formattedGraph;

    /**
     * Counts the number of distinct graphs in the given list of graphs.
     * @param graphs The list of graphs with their individual count.
     * @return The map of distinct graphs and their aggregated count.
     */
    public static Map<String, Long> countDistinctGraphs(List<Map.Entry<String, Long>> graphs) {
        Map<String, Long> distintGraphCounts = new HashMap<String, Long>();
        for (Map.Entry<String, Long> g : graphs) {
            Long count = distintGraphCounts.get(g.getKey());
            if (count == null) {
                count = 0L;
            }
            
            count += g.getValue();
            distintGraphCounts.put(g.getKey(), count);
        }
        
        return distintGraphCounts;
    }
    
    /**
     * Construct a {@link GraphFormat} object.
     * @param type The {@link FormatType} of the graph.
     * @param edges List of edges in the graph.
     * @param vertexCount Number of vertices in the graph.
     * @param useNodeIndexer Whether to use node indexer or not. When node indexer is used, the ids in list of edges are converted to indexed ids before converting to desired format.
     * @deprecated Use other constructor with {@link Mapping} and list of vertices instead
     */
    @Deprecated
    public GraphFormat(FormatType type, List<int[]> edges, int vertexCount, boolean useNodeIndexer) {
        this.formatType = type;
        this.edges = edges;
        this.vertexCount = vertexCount;
        this.mapping = null;
        this.vertices = null;
        this.useNodeIndexer = useNodeIndexer;
        
        this.formattedGraph = null;
    }
    
    /**
     * Construct a {@link GraphFormat} object.
     * @param type  The {@link FormatType} of the graph.
     * @param mapping The {@link Mapping} instance which hold adjacency list representation of the original graph. It will be used to get edges corresponding to given {@code vertices}.
     * @param vertices The list of vertices in the graph.
     */
    public GraphFormat(FormatType type, Mapping mapping, int[] vertices) {
        this.formatType = type;
        this.edges = null;
        this.mapping = mapping;
        this.vertices = vertices;
        this.vertexCount = this.vertices.length;
        this.useNodeIndexer = true; // dummy value, node indexer is not used in this case
        
        this.formattedGraph = null;
    }
    
    /**
     * Convert the graph into g6/s6 format, and hold it in an internal field so that it can be accessed later.
     */
    public void formatGraph() {
        // Create boolean adjacency matrix from the input list of edges or vertices
        boolean[][] adjMatrix = new boolean[this.vertexCount][this.vertexCount];
        
        // List of edges is null, hence use list of vertices along with mapping to construct the adjacency matrix.
        if (this.edges == null) {
            for (int i = 0; i < this.vertexCount; i++) {
                for (int j = i + 1; j < this.vertexCount; j++) {
                    AdjacentVertexWithEdge v = this.mapping.getNeighbour(this.vertices[i], this.vertices[j]);
                    if (v == null) {
                        continue;
                    }

                    // set true if vertex i & j are adjacent
                    adjMatrix[i][j] = true;
                    adjMatrix[j][i] = true;
                }
            }
        }
        // List of edges is given, hence use it directly to construct the adjacency matrix.
        else {
            List<Integer> nodeIndexer = new ArrayList<Integer>(this.vertexCount);
            for (int[] edge : this.edges) {
                int node1 = this.useNodeIndexer ? nodeIndexer.indexOf(edge[0]) : edge[0];
                if (node1 == -1) {
                    node1 = nodeIndexer.size();
                    nodeIndexer.add(node1, edge[0]);
                }

                int node2 = this.useNodeIndexer ? nodeIndexer.indexOf(edge[1]) : edge[1];
                if (node2 == -1) {
                    node2 = nodeIndexer.size();
                    nodeIndexer.add(node2, edge[1]);
                }

                // set true where there is an edge
                adjMatrix[node1][node2] = true;
                adjMatrix[node2][node1] = true;
            }
        }
        
        // get byte representation of vertex count
        byte[] Nn = getBytesRepresentationOfN(this.vertexCount);
        
        // Build a byte string out of the adjacency matrix columnwise
        // Calculate the size of the final byte string
        int formattedGraphSize = Nn.length + this.getByteCountForFormattedGraph();
        
        // Initialize forattedGraph byte string with the calculated size
        this.formattedGraph = new byte[formattedGraphSize];
        
        // copy the byte string representing the vertex count into the final byte string at location 0
        System.arraycopy(Nn, 0, this.formattedGraph, 0, Nn.length);
        
        // If required format type is g6, do this:
        if (this.formatType == FormatType.Graph6) {
            // initialize bit position
            int bitPos = 0;
            // get the next available index in final byte string
            int formattedGraphNextIndex = Nn.length;
            // initialize next bit
            byte formattedGraphNextByte = 0;
            // for each columen in adjacency matrix
            for (int col = 1; col < this.vertexCount; ++col) {
                // for each row in adjacency matrix
                for (int row = 0; row < col; ++row) {
                    // if there is an edge between the two vertices
                    if (adjMatrix[row][col]) {
                        // set corresponding bit to 1
                        formattedGraphNextByte = (byte) (formattedGraphNextByte | (1 << (5 - bitPos)));
                    }
                    // get bit position for every byte
                    bitPos = (bitPos + 1) % 6;
                    
                    // if one byte is complete and bit position goes back to 0
                    // add 63 according to the algorithm
                    if (bitPos == 0) {
                        this.formattedGraph[formattedGraphNextIndex++] = (byte) (formattedGraphNextByte + 63);
                        formattedGraphNextByte = 0;
                    }
                }
            }

            if (formattedGraphNextIndex < formattedGraphSize)
            {
                this.formattedGraph[formattedGraphNextIndex] = (byte) (formattedGraphNextByte + 63);
            }
        }
        // Else if required format type is is s6, do this:
        else {
            // TODO: Implement for FormatType.Sparse6
        }
    }
    
    /**
     * Get the bytes representing the formatted graph
     * @return The list of bytes corresponding to the appropriate format of the graph.
     */
    public byte[] getBytes() {
        if (this.formattedGraph == null) {
            this.formatGraph();
        }
        
        return this.formattedGraph;
    }
    
    
    private int getByteCountForFormattedGraph() {
        if (this.formatType == FormatType.Graph6) {
            int temp = this.vertexCount * (this.vertexCount - 1) / 2;
            return temp/6 + ((temp % 6) > 0 ? 1 : 0);
        }
        else {
            // TODO: Implement for FormatType.Sparse6
            return 0;
        }
    }
    
    private static byte[] getBytesRepresentationOfN(int n) {
        byte[] byteRepresentation;
        if (n <= 62) {
            byteRepresentation = new byte[1];
            byteRepresentation[0] = (byte) (n + 63);
        }
        else if (n <= 258047) {
            byteRepresentation = new byte[4];
            byteRepresentation[0] = 126;
            byteRepresentation[1] = (byte) ((n >>> 12) & 63);
            byteRepresentation[2] = (byte) ((n >>> 6) & 63);
            byteRepresentation[3] = (byte) (n & 63);
        }
        else {
            byteRepresentation = new byte[8];
            byteRepresentation[0] = 126;
            byteRepresentation[1] = 126;
            byteRepresentation[2] = (byte) ((n >>> 30) & 63);
            byteRepresentation[3] = (byte) ((n >>> 24) & 63);
            byteRepresentation[4] = (byte) ((n >>> 18) & 63);
            byteRepresentation[5] = (byte) ((n >>> 12) & 63);
            byteRepresentation[6] = (byte) ((n >>> 6) & 63);
            byteRepresentation[7] = (byte) (n & 63);
        }
        
        return byteRepresentation;
    }
    
    /**
     * Gets the list of vertices contained in this graph.
     * @return The list of vertices.
     */
    public int[] getVertices() {
        if (this.vertices != null) {
            return this.vertices;
        }
        else {
            int[] tempVertices = new int[this.vertexCount];
            int foundVertices = 0;
            for (int[] edge : this.edges) {
                for (int vertex : edge) {
                    boolean containsVertex = false;
                    for (int i = 0; i < foundVertices; i++) {
                        if (tempVertices[i] == vertex) {
                            containsVertex = true;
                            break;
                        }
                    }
                    
                    if (!containsVertex) {
                        tempVertices[foundVertices++] = vertex;
                    }
                }
            }
            
            return tempVertices;
        }
    } 
    
    /**
     * Gets the string representation of the appropriate format type for this graph.
     * @return String representation
     */
    @Override
    public String toString() {
        byte[] formattedGraphBytes = this.getBytes();
        return new String(formattedGraphBytes, Charset.forName("US-ASCII"));
    }
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Arrays.hashCode(this.getBytes());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final GraphFormat other = (GraphFormat) obj;
        if (!Arrays.equals(this.getBytes(), other.getBytes())) {
            return false;
        }
        return true;
    }
    
}
