package edu.uw.nemo.demo;



import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;


import org.apache.commons.collections15.BidiMap;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.scoring.VertexScorer;
import cern.colt.matrix.linalg.EigenvalueDecomposition;
import edu.uci.ics.jung.algorithms.matrix.GraphMatrixOperations;
import cern.colt.matrix.*;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.linalg.*;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import cern.colt.function.*;

import java.util.*;
import edu.uci.ics.jung.algorithms.util.Indexer;
import cern.jet.math.Functions;

/**
 *
 * @author Wooyoung
 */
public class TextFileWriter {
     private String filename;
     
     public TextFileWriter(){
         filename = "output.txt";
         
     }
     public TextFileWriter(String fname){
         filename = fname;
     }
     public int writeMatrix (DoubleMatrix2D adj) throws Exception{
         
                 BufferedWriter writer = null;
        try {
            
            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
       
            //Start writing to the output stream
            for(int i=0; i<adj.rows();i++){
                for (int j=0; j<adj.columns();j++){
                    writer.write(adj.getQuick(i,j) + "\t");
                }
                writer.write("\n");
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
     
    public int writeList2Txtfile(List lst) throws Exception {

        BufferedWriter writer = null;
        try {

            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
       
            //Start writing to the output stream
            
            for(Object elm: lst){
            writer.write(elm + "\n");
                     
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
     
     
     public int writeSet2Txtfile(Set aset) throws Exception{
        
        BufferedWriter writer = null;
        try {
            
            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
       
            //Start writing to the output stream
            
            for(Object elm: aset){
            writer.write(elm + "\n");
                     
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
      
      
     
       public int writeMap2Txtfile(Map amap) throws Exception{
        
        BufferedWriter writer = null;
        try {
            
            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
       
            //Start writing to the output stream
            
            for(Object elm: amap.keySet()){
            writer.write(elm + "\t" + amap.get(elm) + "\n");
                     
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
     
     public int writeString2TXTfile(String string) throws Exception{
        
         System.out.println("about to write" + string);
        BufferedWriter writer = null;
        try {
            
            //Construct the BufferedWriter object
            writer = new BufferedWriter(new FileWriter(filename));
             System.out.println("about to write" + filename);
       
            //Start writing to the output stream
            
           
            writer.write(string + "\n");
                     
            
            
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
      
      
      public static void main(String[] args) {
          
         TextFileReader reader = new TextFileReader("D://RESEARCH//EssentialProtein//Acencio_PPI_merge_data_essential_name.txt");
          try{
          List essset = reader.readList();
          System.out.println("size of essential = " + essset.size());
          
          reader = new TextFileReader("D://RESEARCH//EssentialProtein//Acencio_PPI_merge_data_nonessential_name.txt");
  
          List nonaset = reader.readList();
          System.out.println("size of nonessential = " + nonaset.size());
          
          // Shuffle nonaset
          int num = 19;
          
              Random rgen = new Random(num);  // Random number generator
              int[] indices = new int[nonaset.size()];

//--- Initialize the array to the ints 0-51
              for (int i = 0; i < indices.length; i++) {
                  indices[i] = i;
              }

//--- Shuffle by exchanging each element randomly
              for (int i = 0; i < indices.length; i++) {
                  int randomPosition = rgen.nextInt(indices.length);

                  int temp = indices[i];
                  indices[i] = indices[randomPosition];
                  indices[randomPosition] = temp;

              }
              
              List newset = new ArrayList(essset);
          
          for (int i=0; i<essset.size(); i++) {
             newset.add(nonaset.get(indices[i]));
               
          }
          
           TextFileWriter writer = new TextFileWriter("D://RESEARCH//EssentialProtein//Acencio_PPI_merge_data7.txt");
           writer.writeList2Txtfile(newset);
          
         
            } 
          catch (Exception e){
              e.getStackTrace();
          }
      }


}
