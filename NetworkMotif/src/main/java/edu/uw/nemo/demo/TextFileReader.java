package edu.uw.nemo.demo;



import edu.uci.ics.jung.io.*;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.algorithms.cluster.*;
import edu.uci.ics.jung.graph.util.EdgeType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import cern.colt.matrix.DoubleFactory2D;  
import cern.colt.matrix.DoubleMatrix2D;  
import cern.colt.matrix.impl.*;  
import cern.colt.matrix.*; 
import cern.colt.matrix.linalg.Algebra;  
import cern.colt.matrix.impl.*;
/**
 *
 * @author Wooyoung
 */
public class TextFileReader {
     private String filename;
     public TextFileReader(){
         filename = "test.txt";
     }
     
     public TextFileReader(String fname){
         filename = fname;
     }
     
     public Set readDEGfile() throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           
           Set db = new HashSet();
           
           String line = null;
           while ((line = reader.readLine())!=null){
               try{
                   db.add(line);
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return db;
     }
     
     
      public List readCSVfile() throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           
           List db = new ArrayList();
           
           String line = null;
           while ((line = reader.readLine())!=null){
               try{
                   db.add(line);
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return db;
     }
      
       public List readList() throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           
           List db = new ArrayList();
           
           String line = null;
           while ((line = reader.readLine())!=null){
               try{
                   db.add(line.trim().toUpperCase());
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return db;
     }
     
      public DoubleMatrix1D read1Dmatrix(int size) throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           DoubleMatrix1D mat = new DenseDoubleMatrix1D(size);
           
         
           
           String line = null;
           int i=0;
           while ((line = reader.readLine())!=null){
               try{
                   mat.setQuick(i++, Double.parseDouble(line));
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return mat;
     }
     
     public Map readDIPConverter() throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           
           Map dib = new HashMap();
           
           String line = null;
           
          
           while ((line = reader.readLine())!=null){
               try{
                   String[] columns = line.split("\t");
                 
                   dib.put(columns[0].trim(), columns[1].trim());
                   //System.out.println("line = " + columns[0].trim() + "\t,\t" + columns[1].trim() );
                   
                   
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return dib;
     }
     
      public Map readSyn2SymConverter() throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           
           Map dib = new HashMap();
           
           String line = null;
           
          
           while ((line = reader.readLine())!=null){
               try{
                   String[] columns = line.split("\t");
                 
                   dib.put(columns[0].trim().toUpperCase(), columns[1].trim().toUpperCase());
                   //System.out.println("line = " + columns[0].trim() + "\t,\t" + columns[1].trim() );
                   
                   
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return dib;
     }
      
       public Map readSym2SynConverter() throws Exception{
         
           BufferedReader reader = new BufferedReader(new FileReader(filename));
           
           Map dib = new HashMap();
           
           String line = null;
           
          
           while ((line = reader.readLine())!=null){
               try{
                   String[] columns = line.split("\t");
                   Object sym = columns[1].trim().toUpperCase();
                   Object syn = columns[0].trim().toUpperCase();
                   if(dib.containsKey(sym)) System.out.println(sym + "\t is alreay mapped to \t" + dib.get(sym) + " \t current = \t " + syn );
                 
                   dib.put(sym, syn);
                   //System.out.println("line = " + columns[0].trim() + "\t,\t" + columns[1].trim() );
                   
                   
               }
               catch (Exception e){
                   e.getStackTrace();
               }
           }
           
           return dib;
     }
     
     
      public DoubleMatrix2D readDouble2DMatrix() throws Exception{
          
                  
          
        BufferedReader reader0 = new BufferedReader(new FileReader(filename));
    
    
    // Read an edge of each line from the file. The first node is stored in vnodei, and the second node is in nodej
        
       

       String line = null;
       int rows=0;
       
        while((line = reader0.readLine())!=null){
           try{
              rows++;
            }
            catch (Exception e){
               e.getStackTrace();
            }
       }
       reader0.close();
       
       BufferedReader reader = new BufferedReader(new FileReader(filename));
        double[][] values = new double[rows][];
       int n=0;
       while((line = reader.readLine())!=null){
           try{
               String [] columns = line.split("\t");
               int cols = columns.length;
               values[n] = new double[cols];
               for (int i=0; i<cols; i++) values[n][i] = Double.parseDouble(columns[i]);
             n++;
            }
            catch (Exception e){
               e.getStackTrace();
            }
       }
 
        reader.close();
       DoubleMatrix2D matrix= new DenseDoubleMatrix2D(values);
         //DoubleMatrix2D matrix = new SparseDoubleMatrix2D((double[][])dvec.toArray());
         
     
        return matrix;
    }
      
       public DoubleMatrix2D readDouble2DMatrix(String split) throws Exception{
          
                  
          
        BufferedReader reader0 = new BufferedReader(new FileReader(filename));
    
    
    // Read an edge of each line from the file. The first node is stored in vnodei, and the second node is in nodej
        
       

       String line = null;
       int rows=0;
       
        while((line = reader0.readLine())!=null){
           try{
              rows++;
            }
            catch (Exception e){
               e.getStackTrace();
            }
       }
       reader0.close();
       
       BufferedReader reader = new BufferedReader(new FileReader(filename));
        double[][] values = new double[rows][];
       int n=0;
       while((line = reader.readLine())!=null){
           try{
               String [] columns = line.split(split);
               int cols = columns.length;
               values[n] = new double[cols];
               for (int i=0; i<cols; i++) values[n][i] = Double.parseDouble(columns[i]);
             n++;
            }
            catch (Exception e){
               e.getStackTrace();
            }
       }
 
        reader.close();
       DoubleMatrix2D matrix= new SparseDoubleMatrix2D(values);
         //DoubleMatrix2D matrix = new SparseDoubleMatrix2D((double[][])dvec.toArray());
         
     
        return matrix;
    }
      
     public static void main(String[] args) {

        String path = "D:\\RESEARCH\\EssentialProtein\\";


        try {
            TextFileReader reader = new TextFileReader(path + "Acencio_PPI_symbol_list.txt");

            List ppilist = reader.readList();
         
        
            System.out.println("size of symbol list = " + ppilist.size());
            
            reader = new TextFileReader(path + "Essential_symbol_update.txt");
            
            List essList = reader.readList();
            
            
            reader = new TextFileReader(path + "Non_Essential_symbol_update.txt");
            
            List nonEssList = reader.readList();
            
            List newclass = new ArrayList();
            
          int i=0;
            
            for(Object gene:ppilist){
                String essential = "";
                if((essList.contains(gene)) && (nonEssList.contains(gene))) {
                    System.out.println(gene + "\t" + "at the same time");
                    i++;
                    
                }
                if(essList.contains(gene)) essential = "Essential";
                else if (nonEssList.contains(gene)) essential = "NonEssential";
                else essential = "UnKnown";
                
                newclass.add(gene+"\t" + essential);
                
                
            }
            System.out.println("Both classes = " + i);
         
            TextFileWriter writer = new TextFileWriter(path + "Acencio_PPI_new class_list.txt");
            writer.writeList2Txtfile(newclass);
            
                       


          /*  reader = new TextFileReader(path + "ep_weka_list.csv");
            List mylist = reader.readList();

            reader = new TextFileReader(path + "ep_weka_data..txt");
            List mydata = reader.readList();


            List<String> newlist = new ArrayList<String>();


            for (Object sym : list) {
                String protein = (String) sym;
                Object data = "null";
                if (mylist.contains(sym)) {
                    int idx = mylist.indexOf(sym);
                    data = mydata.get(idx);

                }
                newlist.add(protein + "\t" + data);

            }


            TextFileWriter writer = new TextFileWriter(path + "merge_data.txt");
            writer.writeList2Txtfile(newlist);*/



            /*     reader = new TextFileReader("D://JavaWork//datafiles//yeast20101010_converter.txt");
            Map synmap = reader.readSyn2SymConverter();
            List symlist = new ArrayList();
            List<List> maplist = new ArrayList<List>();
            for (Object syn: synlist){
            List couple = new ArrayList();
            couple.add(syn);
            couple.add(synmap.get(syn));
            
            symlist.add(synmap.get(syn));
            maplist.add(couple);
            }
            
            Set symset = new HashSet(symlist);
            System.out.println("size of genes = " + symset.size());
            System.out.println("size of interactions = " + symlist.size());
            
            TextFileWriter writer = new TextFileWriter("D://RESEARCH//EssentialProtein//Yeast_TR_Symbol.txt");
            writer.writeList2Txtfile(symlist);*/


        } catch (Exception e) {
            e.printStackTrace();
            e.getStackTrace();
        }
    }
}
