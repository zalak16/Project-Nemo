package edu.uw.nemo.demo;


import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.EdgeType;

import edu.uci.ics.jung.graph.Graph;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections15.Factory;

/**
 *
 * @author Wooyoung
 */
public class GraphFileReader {
    /** Read a graph file**/
    
    private String filename;
    private boolean directed;

    public GraphFileReader(String name)
    {
        filename = name;
       
    };
    
    public GraphFileReader()
    {
         directed = false;
    };
          
    public boolean getDirect()
    {
        return directed;
    }
    
    public Graph readTXTfile(boolean dir) throws Exception{
        
        directed = dir;
        
        Graph g;
               
        if (directed) g = new DirectedSparseGraph();
        else g = new UndirectedSparseGraph();
        
        BufferedReader reader = new BufferedReader(new FileReader(filename));
    
    
    // Read an edge of each line from the file. The first node is stored in vnodei, and the second node is in nodej
      
     
       String line = null;
       int n=0;
        while((line = reader.readLine())!=null){
           String first, second;
           Set edgeSet = new HashSet();
           double weight=1.0;
           try{
               String[] columns = line.split("\t");
               if (columns.length<2) columns = line.split(" ");
               first =columns[0]; second = columns[1];
               first.trim();  second.trim();
               if (first.equals(second))continue;
               List pair = new ArrayList();
               pair.add(first); pair.add(second);    
               if (columns.length > 2) weight = Double.parseDouble(columns[2]);
               if (directed) {
                   if (edgeSet.contains(pair)) continue;
                   g.addEdge(n,pair, EdgeType.DIRECTED);
                   
               }
               else {
                    Collections.sort(pair); 
                    if (edgeSet.contains(pair)) continue;
                    g.addEdge(n, pair, EdgeType.UNDIRECTED);
               }
               edgeSet.add(pair);
             n++;
            }
            catch (Exception e){
               e.getStackTrace();
            }
       }

        reader.close();
        
      // System.out.println("G: = " + g + " E=" +  g.getEdgeCount() + "  V = " + g.getVertexCount());
        return g;
    }

    public Graph readPajetNet() throws Exception{
        
        int idx = filename.indexOf("directed");
        if(idx>0) directed = true;
        else directed = false;
        
        Graph g;
        
        Factory<String> vertexFactory = new Factory<String>() {
         int vn = 0;
         public String create() { return Integer.toString(vn++); }
        };
        
   
        Factory<Integer> edgeFactory = new Factory<Integer>() {
         int en = 0;
         public Integer create() { return en++; }
        };
        
       
        PajekNetReader pnr = new PajekNetReader(vertexFactory, edgeFactory);
        
        if (directed)   g = new DirectedSparseGraph();
        else g = new UndirectedSparseGraph();
        
        try {
                pnr.load(filename, g);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
	return g;
      
    }
    
    public Graph readGraphML() throws Exception{
        
        Graph g;

        g = new DirectedSparseMultigraph();
        
        Factory<String> vertexFactory = new Factory<String>() {
         int vn = 0;
         public String create() { return Integer.toString(vn++); }
        };
        
        Factory<Integer> edgeFactory = new Factory<Integer>() {
         int en = 0;
         public Integer create() { return en++; }
        };
        
    	GraphMLReader gmlr = new GraphMLReader(vertexFactory, edgeFactory);
    	
    	gmlr.load(filename, g);
                
        return g;
    }
    
    public Graph fileRead (boolean dir) throws Exception{
        Graph g = null;
           
        int idx = filename.indexOf(".");
        String inFormat = filename.substring(idx+1);
        
       
         if (inFormat.equalsIgnoreCase("net"))g = readPajetNet();
         else if (inFormat.equalsIgnoreCase("graphml")) g = readGraphML();
         else {             
                directed = false;
            g = readTXTfile(dir);
          }
         
         return g;
    }

       public static void main(String[] args) throws Exception{  
    
           /** The main function is just a test function*/
           
       GraphFileReader myReader = new GraphFileReader("Scere20140117CR.txt");

       Graph g=myReader.fileRead(false);
       System.out.println("|V| = " + g.getVertexCount() + ", |E|=" + g.getEdgeCount());
       }

}

