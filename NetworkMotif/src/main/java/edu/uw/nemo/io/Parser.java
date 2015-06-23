package edu.uw.nemo.io;

import edu.uw.nemo.model.AdjacencyMapping;
import edu.uw.nemo.model.Mapping;

import java.io.*;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * It reads the input fie and creates the in-memory mapping object
 * which facilitates working with graph represented in the file.
 * @author joglekaa
 * @author vartikav
 */
public class Parser {

    /**
     * Reads the file and constructs the mapping object.
     * @param fileName The file which contains the input graph.
     * @return The mapping object which hold in-memory structure to work with the input graph.
     * @throws IOException
     * @throws URISyntaxException
     */
    public Mapping parser(String fileName) throws IOException, URISyntaxException {
        InputStream fileInputStream = openInputFile(fileName);
        return map(readFile(fileInputStream));
    }
    
    /**
     * Reads the stream and constructs the mapping object.
     * @param stream The stream which contains the input graph.
     * @return The mapping object which hold in-memory structure to work with the input graph.
     * @throws IOException
     * @throws URISyntaxException
     */
    public Mapping parser(InputStream stream) throws IOException, URISyntaxException {
        return map(readFile(stream));
    }

    // Reads file line wise and splits each line into two nodes.
    // Returns the list of pair of nodes which represent edges.
    private List<String[]> readFile(InputStream stream) throws IOException {
        //time start
        long start = System.currentTimeMillis();
        // List of 2 nodes per line
        List<String[]> result = new ArrayList<String[]>();
        BufferedReader input = new BufferedReader(new InputStreamReader(stream));
        // each line of the input file
        String line = null;
        //read each line in the input file
        while ((line = input.readLine()) != null) {
            //split it when \t is encountered. Line would be split into 2 if not null
            String[] split = line.trim().split("\t");
            String node1 = split[0];
            String node2 = split[1];
            if (node1 != null && node2 != null) {
                split[0] = node1.trim();
                split[1] = node2.trim();
                result.add(split);
            }
        }
        // time stop
        System.out.println("loading for file took " + (System.currentTimeMillis() - start) + " milliseconds.");
        return result;
    }
    
    /* 
    * Creates Adjacency mapping between the nodes as read from the input file
    * Also maps originial represenation of a node to its integer representation 
    * map string -> string  (node to node)
    * map string -> int     (node original to node int)
    * map int -> string     (node int to node original)
    * Returns a Mapping object and takes in adjacency list of 2 nodes
    */
    private Mapping map(List<String[]> aL) {
        // time start
        long start = System.currentTimeMillis();
        // node as read from inut file mapped to its integer representation
        Map<String, Integer> nodeToId = new HashMap<String, Integer>();
        // vice versa - integer representation mapped to node as read from inut file
        Map<Integer, String> idToNode = new HashMap<Integer, String>();
        // total node count in the input file
        int nodeCount = getNodeCount(aL);
        // Initialize adjacency list with the number of nodes
        AdjacencyMapping adjMapping = new AdjacencyMapping(nodeCount);
        int count = 0;
        // for each list of 2 nodes in the input
        for (String[] link : aL) {
            // check whether the node has already been IDed, if not then ID it
            int id1 = mapNode(nodeToId, idToNode, count, link[0]);
            if (id1 == count) {
                count++;
            }
            int id2 = mapNode(nodeToId, idToNode, count, link[1]);
            if (id2 == count) {
                count++;
            }
            // add the nodes to the adjacency list
            adjMapping.addAdjacentVertices(id1, id2);
        }
        // time stop
        System.out.println("mapping to ids took " + (System.currentTimeMillis() - start) + " milliseconds.");
       
        return new Mapping(aL, adjMapping);
    }
    
    // Counts the number of nodes in the input adjacency list.
    // Takes in a list of 2 nodes per line.
    // Returns an integer count.
    private int getNodeCount(List<String[]> aL) {
        // Set of seen nodes to avoid recounting them
        Set<String> seenIds = new HashSet<String>();
        int count = 0;
        // for each set of 2 nodes per line
        for (String[] link : aL) {
            // if first node has been seen before then skip, else count it
            if (!seenIds.contains(link[0])) {
                seenIds.add(link[0]);
                count++;
            }
            // if second node has been seen before then skip, else count it
            if (!seenIds.contains(link[1])) {
                seenIds.add(link[1]);
                count++;
            }
        }
        
        return count;
    }

    // Maps te given node to its integer id and return it.
    // It first tries to lookup the existing map of node to its id, and if not found create a new entry.
    private int mapNode(Map<String, Integer> nodeToId, Map<Integer, String> idToNode, int count, String node) {
        Integer id = nodeToId.get(node);
        if (id == null) {
            id = count;
            nodeToId.put(node, id);
            idToNode.put(id, node);
        }
        return id;
    }

    // Opens the srcFile and return a InputStream created around it.
    private InputStream openInputFile(String srcFile) throws IOException, URISyntaxException {
        URL url =
                Thread.currentThread().getContextClassLoader().getResource(srcFile);
        if (url == null) {
            return new FileInputStream(srcFile);
        }
        else {
            Path path = Paths.get(url.toURI());
            return Files.newInputStream(path);
        }
    }

}
