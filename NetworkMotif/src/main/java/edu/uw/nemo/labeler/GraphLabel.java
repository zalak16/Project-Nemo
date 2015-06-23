/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.labeler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;

import edu.uw.nemo.model.Mapping;

/**
 * Creates the canonical labeling for a given graph using nauty library's tool: labelg
 * @author vartikav
 */
public class GraphLabel {
    
    // input subgraphs found by ESU algorithm
    // map of g6 representation to count of sub-graphs with this g6 representation 
    private Map<String, Long> subGraphs;
    
    // A writer to save all enumerated sub-graphs
    private BufferedWriter enumeratedGraphsWriter;
    
    // File to store all the enumerated sub-graphs
    private final static String EnumeratedGraphsFile = "EnumeratedGraphs.txt";
    
    // input file for labelg.exe containing graphs in g6 format
    private final static String LabelGInputFile = "InputGraphs.g6";

    
    // output file for labelg.exe containing canonical labels
   // private final static String LabelGOutputFile = "./OutputGraphs.g6";
    private final static String LabelGOutputFile = "OutputGraphs.g6";

    
    /**
     * Constructs an instance of GraphLabel with option to save the generated sub-graphs
     * @param saveSubGraphs If true, will save any subGraph added to an intermediate file.
     */
    public GraphLabel(boolean saveSubGraphs) {
        this.subGraphs = new LinkedHashMap<String, Long>();
        if (saveSubGraphs) {
            try {
                this.enumeratedGraphsWriter = new BufferedWriter(new FileWriter(EnumeratedGraphsFile, false));
            }
            catch (IOException ioe) {
                System.err.println("Failed to create file to save enumerated sub-graphs. " + ioe.getMessage());
                this.enumeratedGraphsWriter = null;
            }
        }
        else {
            this.enumeratedGraphsWriter = null;
        }
    }


    /**
     * Close the BufferedWriter used for saving subgraphs.
     */
    public void close() {
        if (this.enumeratedGraphsWriter != null) {
            try {
                this.enumeratedGraphsWriter.close();
            }
            catch (IOException ioe) {
                System.err.println("Failed while closing file to save enumerated sub-graphs. " + ioe.getMessage());
            }
            finally {
                this.enumeratedGraphsWriter = null;
            }
        }
    }

    @Override
    protected void finalize() throws Throwable {
        try {
            this.close();
        }
        finally {
            super.finalize();
        }
    }
    
    /**
     * Clear all the subgraphs added till now.
     */
    public void clear() {
        this.subGraphs.clear();
    }
    
    /**
     * Get the number of subgraphs currently hold by this instance of {@link GraphLabel}.
     * @return The number of subgraphs.
     */
    public long getSubgraphCount() {
        long count = 0;
        for (Long subGraphCount : this.subGraphs.values()) {
            count += subGraphCount;
        }
        
        return count;
    }

    /**
     * Adds a subgraph after formating it into g6 representation.
     * @param edges The list of edges in the subgraph which is getting added.
     * @param vertexCount The number of vertices in the subgraph which is getting added.
     * @deprecated Use {@code void addSubGraph(Mapping mapping, int[] vertices)} instead
     */
    @Deprecated
    public void addSubGraph(List<int[]> edges, int vertexCount) {
        // Instantiate a graphformat object with input edges and vertices
        GraphFormat graphFormat = new GraphFormat(FormatType.Graph6, edges, vertexCount, true);
        
        // format into g6 representation
        graphFormat.formatGraph();
        Long graphCount = this.subGraphs.get(graphFormat.toString());
        if (graphCount == null) {
            graphCount = 0L;
        }
        
        this.subGraphs.put(graphFormat.toString(), ++graphCount);
        if (this.enumeratedGraphsWriter != null) {
            this.saveSubGraph(graphFormat);
        }
    }

    /**
     * Adds a subgraph after formating it into g6 representation.
     * @param mapping The {@link Mapping} instance which hold adjacency list representation of the original graph. It will be used to get edges corresponding to given {@code vertices}.
     * @param vertices The list of vertices in the subgraph which is getting added.
     */
    public void addSubGraph(Mapping mapping, int[] vertices) {
        // Instantiate a graphformat object with input vertices
        GraphFormat graphFormat = new GraphFormat(FormatType.Graph6, mapping, vertices);
        
        // format into g6 representation
        graphFormat.formatGraph();
        Long graphCount = this.subGraphs.get(graphFormat.toString());
        if (graphCount == null) {
            graphCount = 0L;
        }
        
        this.subGraphs.put(graphFormat.toString(), ++graphCount);
        if (this.enumeratedGraphsWriter != null) {
            this.saveSubGraph(graphFormat);
        }
    }
    
    /**
     * Computes the canonical label for the given graph in g6 format
     * @param graphInG6Format
     * @return Canonical label for the graph on success. On any failure, returns null
     */
    public static String getCanonicalLabel(String labelgPath, String graphInG6Format) {
        // create a set with single graphInG6Format to call generateCanonicalLabels
        Set<String> graphLabels = new HashSet<String>();
        graphLabels.add(graphInG6Format);
        
        Map<String, String> canonicalLabels = generateCanonicalLabels(labelgPath, graphLabels);
        if (canonicalLabels == null || !canonicalLabels.containsKey(graphInG6Format)) {
            return null;
        }
        
        return canonicalLabels.get(graphInG6Format);
    }

    /**
     * Computes the canonical label for all the sub-graphs that it currently holds.
     * The list of original graphs, after aggregation, is the value of the map entry.
     * Thus it aggregate all canonical sub-graphs together.
     * @return The map from canonically labeled sub-graph to a list of all the representative sub-graphs for that canonical label with the frequency of occurrence.
     */
    public Map<String, List<Map.Entry<String, Long>>> getCanonicalLabels() {
        // (final output) canonicalLabels maps each canonical label to graphs in graph6 format.
        Map<String, List<Map.Entry<String, Long>>> canonicalLabels = new HashMap<String, List<Map.Entry<String, Long>>>();
        // (intermediate output) maps graph6 string to canonical string obtained from labelg.exe
        Map<String, String> graphWithCanonicalLabels = generateCanonicalLabels("labelg.exe", this.subGraphs.keySet());
        
        for (Map.Entry<String, Long> subGraph : this.subGraphs.entrySet()) {
            String canonicalLabel = graphWithCanonicalLabels.get(subGraph.getKey());
            List<Map.Entry<String, Long>> canonicalGraphs = canonicalLabels.get(canonicalLabel);
            if (canonicalGraphs == null) {
                canonicalGraphs = new ArrayList<Map.Entry<String, Long>>();
                canonicalLabels.put(canonicalLabel, canonicalGraphs);
            }
            
            canonicalGraphs.add(subGraph);
        }
        
        return canonicalLabels;
    }

    /**
     * Computes the canonical label for all the sub-graphs that it currently holds.
     * The list of original graphs, after aggregation, is the value of the map entry.
     * Thus it aggregate all canonical sub-graphs together.
     * @return The map from canonically labeled sub-graph to a list of all the representative sub-graphs for that canonical label with the frequency of occurrence.
     */
    public Map<String, List<Map.Entry<String, Long>>> getCanonicalLabels(String labelFileName) {
        // (final output) canonicalLabels maps each canonical label to graphs in graph6 format.
        Map<String, List<Map.Entry<String, Long>>> canonicalLabels = new HashMap<String, List<Map.Entry<String, Long>>>();
        // (intermediate output) maps graph6 string to canonical string obtained from labelg.exe
        Map<String, String> graphWithCanonicalLabels = generateCanonicalLabels(labelFileName, this.subGraphs.keySet());

        for (Map.Entry<String, Long> subGraph : this.subGraphs.entrySet()) {
            String canonicalLabel = graphWithCanonicalLabels.get(subGraph.getKey());
            List<Map.Entry<String, Long>> canonicalGraphs = canonicalLabels.get(canonicalLabel);
            if (canonicalGraphs == null) {
                canonicalGraphs = new ArrayList<Map.Entry<String, Long>>();
                canonicalLabels.put(canonicalLabel, canonicalGraphs);
            }

            canonicalGraphs.add(subGraph);
        }

        return canonicalLabels;
    }
    
    private static Map<String, String> generateCanonicalLabels(String labelgPath, Set<String> graphLabels)
    {
        System.out.println("Total number of unique sub-graphs for which canonical labels need to be computed: " + graphLabels.size());
        // Create a file with all the entries from graphLabels, one at each line.
        BufferedWriter graphWriter = null;
        try {
            graphWriter = new BufferedWriter(new FileWriter(LabelGInputFile));

            System.out.println("LabelG Input file created");
            for (String graphLabel : graphLabels) {
                graphWriter.write(graphLabel);
                graphWriter.write('\n');
            }
        }
        catch (IOException ioe) {
            System.err.println("Error while writing graphs for labelg. Error msg: " + ioe.getMessage());
            return null;
        }
        finally {
            if (graphWriter != null) {
                try {
                    graphWriter.close();
                }
                catch (IOException ioe) {
                    System.err.println("Error while writing graphs for labelg. Error msg: " + ioe.getMessage());
                    return null;
                }
            }
        }
        
        // Call labelg.exe with the file formed and a output file.
        try {
            String[] args = {labelgPath, "-i3", "-I1:100", LabelGInputFile, LabelGOutputFile};
            Process labelg = Runtime.getRuntime().exec(args);

            OutputStream out = labelg.getOutputStream();

            out.close();

            BufferedReader inStr = new BufferedReader(new java.io.InputStreamReader(labelg.getInputStream()));
            String line = inStr.readLine();
            while (line != null) {
                line = inStr.readLine();
            }
            
            inStr.close();
            
            BufferedReader errStr = new BufferedReader(new java.io.InputStreamReader(labelg.getErrorStream()));
            line = errStr.readLine();
            while (line != null) {
                line = errStr.readLine();
            }
            
            errStr.close();
            
            int returnCode = -1;
            try {
                returnCode = labelg.waitFor();
                System.out.println("Return code for labelg: " + returnCode);
            }
            catch (InterruptedException ie) {
                System.err.println("Error while generating canonical labels. Error msg: " + ie.getMessage());
            }
        }
        catch (IOException ioe) {
            System.err.println("Error while generating canonical labels. Error msg: " + ioe.getMessage());
        }
        
        // Read the outputfile to get the canonical label for each graphFormat
        Map<String, String> canonicalLabels = new HashMap<String, String>();
        BufferedReader canonicalLabelReader = null;
        try {
            canonicalLabelReader = new BufferedReader(new FileReader(LabelGOutputFile));
            Iterator<String> itr = graphLabels.iterator();

            String label = canonicalLabelReader.readLine();
            while (label != null)
            {
                canonicalLabels.put(itr.next(), label);
                label = canonicalLabelReader.readLine();
            }
        }
        catch (IOException ioe) {
            System.err.println("Error while reading canonical labels produced by labelg. Error msg: " + ioe.getMessage());
            return null;
        }
        finally {
            if (canonicalLabelReader != null) {
                try {
                    canonicalLabelReader.close();
                }
                catch (IOException ioe) {
                    System.err.println("Error while reading canonical labels produced by labelg. Error msg: " + ioe.getMessage());
                    return null;
                }
            }
        }
        
        return canonicalLabels;
    }
    
    // Save the added subgraph in an intermediate file
    private void saveSubGraph(GraphFormat subGraph) {
        int[] vertices = subGraph.getVertices();
        StringBuilder strBuilder = new StringBuilder();
        for (int vertex : vertices) {
            strBuilder.append(vertex);
            strBuilder.append(',');
        }
        
        try {
            this.enumeratedGraphsWriter.write(strBuilder.toString(), 0, strBuilder.length() - 1);
            this.enumeratedGraphsWriter.write('\t');
            String g6Representation = subGraph.toString();
            this.enumeratedGraphsWriter.write(g6Representation, 0, g6Representation.length());
            this.enumeratedGraphsWriter.newLine();
        }
        catch (IOException ioe) {
            System.err.println("Error while saving sub-graph to file. " + ioe.getMessage());
            this.enumeratedGraphsWriter = null;
        }
    }
}
