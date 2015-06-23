package edu.uw.nemo;

import edu.uw.nemo.esu.ESUGen;
import edu.uw.nemo.io.Parser;
import edu.uw.nemo.labeler.GraphFormat;
import edu.uw.nemo.labeler.GraphLabel;
import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.motifSignificant.CalculateMotifSignificance;
import edu.uw.nemo.motifSignificant.SubgraphConcentration;
import edu.uw.nemo.motifSignificant.explicitMethod.NemoControllerRandomGraphs;
import edu.uw.nemo.motifSignificant.explicitMethod.RandomGraphCanonicalLabelling;
import edu.uw.nemo.motifSignificant.explicitMethod.SwitchingAlgorithm.SwitchingAlgoirthmGenerateGraph;
import edu.uw.nemo.motifSignificant.explicitMethod.preserveNumberOfVertexAndEdges.GenerateGraph;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Extracts significant subgraphs using ESU algorithm and DIRECT calculation of concentration
 * @author joglekaa
 * @author vartikav
 */
public class NemoController {


     /**
     * Enumerates the sub-graphs of given size from the graph specified by
     * the input file. Then it computes the string representation of each of the sub-graph
     * using g6 representation as defined by nauty library. Finally it computes
     * the canonical label of each sub-graph and aggregates them based on it. Thus,
     * creating a map from canonically labeled sub-graph to a list of all the representative
     * sub-graphs for that canonical label with the frequency of occurrence.
     * @param fileName A file containing the graph as list of edges. Each line is an edge represented by a pair of vertices.
     * @param size The size of the sub-graphs to generate.
     * @return The map of all the canonically labeled sub-graphs of given size found.
     */
    public Map<String, List<Map.Entry<String, Long>>> extract(String fileName, int size, int totalRandomGraph, double probability) {
        // build Mapping with parser
        Mapping mapping = this.parseFile(fileName);
        System.out.println("#Nodes: " + mapping.getNodeCount() + " #Edges: " + mapping.getLinkCount());
        // generate motifs with ESUGen for the input subgraph size
        GraphLabel label = new GraphLabel(false);
        this.enumerateSubGraphs(mapping, label, size);
        System.out.println("Number of subgraphs enumerated: " + label.getSubgraphCount());
        
        // get canonical labels with GraphLabel
        long start = System.currentTimeMillis();
        Map<String, List<Map.Entry<String, Long>>> canonicalSubgraphs = label.getCanonicalLabels();
        System.out.println("Generating canonical labels took " + (System.currentTimeMillis() - start) + " milliseconds.");
        System.out.println("Number of canonical labels for all enumerated subgraphs: " + canonicalSubgraphs.size());
        for ( Map.Entry<String, List<Map.Entry<String, Long>>> e : canonicalSubgraphs.entrySet()) {
            System.out.println("Cannonical Label (g6) \"" + e.getKey() + "\" has following Sub Graphs:");
            Map<String, Long> subGraphCounts = GraphFormat.countDistinctGraphs(e.getValue());
            for (Map.Entry<String, Long> c : subGraphCounts.entrySet()) {
                System.out.println("\tSubGraph (g6) \"" + c.getKey() + "\" has count: " + c.getValue());
            }
        }
        

        long startTime = System.currentTimeMillis();
        // Generate 1,000 random graphs and enumerate sample subgraphs in each of the random graphs
        ArrayList<RandomGraphCanonicalLabelling> randomGraphLabel=  this.randomGraphGeneration(mapping, size, totalRandomGraph, probability);

        long endTime = System.currentTimeMillis();

        System.out.println("zTime taken for " + totalRandomGraph + " Random graph: " + (endTime - startTime));

        //Calculate Z-score or p-value and print motif significance
        this.printSignificanceMotif(randomGraphLabel, canonicalSubgraphs, totalRandomGraph, size, probability);

        return canonicalSubgraphs;
    }

    private void printSignificanceMotif(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabel,  Map<String, List<Map.Entry<String, Long>>> inputGraphLabel, int totalRandomGraph, int k, double prob)
    {
       // CalculateMotifSignificance motifSignificance = new CalculateMotifSignificance();
        //motifSignificance.printSignificantMotif(randomGraphLabel, inputGraphLabel, prob, totalRandomGraph, k);
        SubgraphConcentration sc = new SubgraphConcentration();
        sc.printSignificantMotif(randomGraphLabel, inputGraphLabel, totalRandomGraph, k, prob);
    }
    private Mapping parseFile(String fileName)
    {
        Parser parser = new Parser();
        Mapping mapping = null;
        try {
            mapping = parser.parser(fileName);
        }
        catch (IOException ioe) {
            System.err.println("Exception while parsing file for mapping. " + ioe.getLocalizedMessage());
        }
        catch (URISyntaxException urie) {
            System.err.println("Exception while parsing file for mapping. " + urie.getLocalizedMessage());
        }
        
        return mapping;
    }

    /**
     * Generate random graphs using either of the algorithms Switching algorithms or algorithm preserving # of edges and vertices
     * and enumerate sample subgraphs and label.
     * @param inputMapping : Input network mapped into mapping object
     * @param k : subgraph size
     * @param totalRandomGraph : number of random graphs to be generate
     * @param prob : probability of sample subgraphs to be generated
     * @return
     */
    private ArrayList<RandomGraphCanonicalLabelling>  randomGraphGeneration(Mapping inputMapping, int k, int totalRandomGraph, double prob)
    {
        NemoControllerRandomGraphs nemoRand = new NemoControllerRandomGraphs();
        //Switching algorithm preserving degree sequence
       return nemoRand.randomGraphGenerationSwitchingAlgorithm(inputMapping, k, totalRandomGraph, prob);

        //call to algorithm preserving number of edges and vertices
        //return nemoRand.randomGraphGenerationAlgorithm1(inputMapping, k, totalRandomGraph, prob);
    }

    private void enumerateSubGraphs(Mapping mapping, GraphLabel label, int size) {
        ESUGen generator = new ESUGen(true);
        generator.enumerateSubgraphs(mapping, label, size);
    }
}
