package edu.uw.nemo.demo;


/**
 *
 * @author Wooyoung
 */

import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.*;
import edu.uci.ics.jung.graph.util.Pair;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import edu.uci.ics.jung.algorithms.util.Indexer;
import org.apache.commons.collections15.BidiMap;

public class GraphFileWriter {
    
   
    private Graph g;
    private String filename;
    
    
      public GraphFileWriter(Graph gh)
    {
        g = gh;
       
    };
    
     public GraphFileWriter()
    {
    };
    
    
    
    public GraphFileWriter(Graph gh, String fname)
    {
        filename = fname;
        g = gh;
    };
    
  
     public void setFilename(String fname)
    {
        filename = fname;
    }
     
     public int writeTXTindexFile(Graph g, String filename) throws Exception{
         
         BidiMap indexer = Indexer.create(g.getVertices());
        
        BufferedWriter writer = null;
        Collection Edges= g.getEdges(); 
        
            
           try {
            
            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
       
            //Start writing to the output stream
            
            for (Iterator eIt = Edges.iterator(); eIt.hasNext();){
          
            Object anedge = eIt.next();
            
            Pair vertices = g.getEndpoints(anedge);
            
            Object first = vertices.getFirst();
            Object second = vertices.getSecond();
           
           
            writer.write(indexer.get(first) + "\t" + indexer.get(second));
            writer.newLine();
           
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return -1;
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            //Close the BufferedWriter
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return -1;
            }
        }


        return 0;
     }
    
     public int writeTXTfile(Graph g, String filename) throws Exception{
        
        BufferedWriter writer = null;
        Collection Edges= g.getEdges(); 
        
            
           try {
            
            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
       
            //Start writing to the output stream
            
            for (Iterator eIt = Edges.iterator(); eIt.hasNext();){
          
            Object anedge = eIt.next();
            
            Pair vertices = g.getEndpoints(anedge);
            
            Object first = vertices.getFirst();
            Object second = vertices.getSecond();
           
           
            writer.write(first + "\t" + second);
            writer.newLine();
           
            }
            
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return -1;
        } catch (IOException ex) {
            ex.printStackTrace();
            return -1;
        } finally {
            //Close the BufferedWriter
            try {
                if (writer != null) {
                    writer.flush();
                    writer.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
                return -1;
            }
        }


        return 0;
     }
     
      public void writePajetNet(Graph gr, String filename) throws Exception{
          
      
        PajekNetWriter pnw = new PajekNetWriter();
        
        
         
        try {
                pnw.save(gr, filename);
		} catch (IOException ioe) {
			System.err.println(ioe);
		}
   
      
    }
      
      public void writeGraphML(Graph gr, String filename) throws Exception{
          
       	GraphMLWriter gmlw = new GraphMLWriter();
        
        try{
            FileWriter fw = new FileWriter(filename);
            gmlw.save(gr, fw);
        } catch (IOException ioe) {
			System.err.println(ioe);
		}
   
      
    }
      
      public void fileWrite () throws Exception{
          
        int idx = filename.indexOf(".");
        String inFormat = filename.substring(idx+1);
         
         if (inFormat.equalsIgnoreCase("net")) writePajetNet(g, filename);
         else if (inFormat.equalsIgnoreCase("graphml"))writeGraphML(g, filename);
         else   
             writeTXTfile(g, filename);
          
    
}
      
     public static void main(String[] args) {
        
        Graph g=null;

  
        
        
        //GraphFileReader reader = new GraphFileReader("coli1_1_transcription.txt");
        
        GraphFileReader reader = new GraphFileReader("DIPCore_yeast.txt");
        try{
            g = reader.fileRead(false); 
             GraphFileWriter fwriter = new GraphFileWriter(g);
             fwriter.writeTXTindexFile(g, "DIPCore_yeast_index.txt");
        }
        catch (Exception e){
            System.out.println("Error in loading graph");
            e.printStackTrace();
        }
       
        
       
         
         //System.out.println(motifSet + "   : Time = " + (endTime-startTime)/1000000000.0 + " seconds" + "\n motifset=" + motifSet.size());
     
        return;
      }
      


}
