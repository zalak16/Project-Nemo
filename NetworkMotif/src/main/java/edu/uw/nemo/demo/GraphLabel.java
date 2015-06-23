package edu.uw.nemo.demo;


import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import cern.colt.matrix.impl.*;
import cern.colt.matrix.DoubleMatrix2D;
import edu.uci.ics.jung.graph.Graph;
//import edu.uci.ics.jung.algorithms.matrix.GraphMatrixOperations.*;
import edu.uci.ics.jung.algorithms.matrix.*;

import java.io.*;

/**
 *
 * @author Wooyoung
 */

public class GraphLabel { 

// Returned a label of given graph
    
    public static Map<String, Integer> getMotifsEachProcess(Map<String, Integer> motifSet){
        Map<String, Integer> motifMap = new HashMap<String, Integer>();
          Set<String> stringset= motifSet.keySet() ; 
     Iterator iter = stringset.iterator() ; 
     
      String output=null;
    OutputStream stdin = null;
    InputStream stderr = null;
    InputStream stdout = null;
        
         while (iter.hasNext())  {  
      
         String skey = (String) iter.next();
         Integer count = motifSet.get(skey);
         
         
         try{

      // launch EXE and grab stdin/stdout and stderr
      Process process = Runtime.getRuntime ().exec ("labelg.exe -i3 -I1:100\n");
     // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
      stdin = process.getOutputStream ();
      stderr = process.getErrorStream ();
      stdout = process.getInputStream ();
  
      // "write" the parms into stdin
      String input =skey + "\n";   
      stdin.write(input.getBytes() );
      stdin.flush();

      stdin.close();
        String line;
       // clean up if any output in stdout
      BufferedReader brCleanUp = 
        new BufferedReader (new InputStreamReader (stdout));
      while ((line = brCleanUp.readLine ()) != null) {
        //System.out.println ("[Stdout] " + line);
        output = line;
        
      }
      brCleanUp.close();
    
      // clean up if any output in stderr
      brCleanUp = new BufferedReader (new InputStreamReader (stderr));
      while ((line = brCleanUp.readLine ()) != null) {
       // System.out.println ("[Stderr] " + line);
       
      }
      brCleanUp.close();
  
    }
    catch (Exception err) {
      err.printStackTrace();
    }
         
        
          if (!motifMap.containsKey(output)) motifMap.put(output, count); 
         else{
              motifMap.put(output, motifMap.get(output)+count);
         }  
      
     }
    
    //System.out.println(motifMap);
        
        
    
        
        return motifMap;
    }
    
     public static Map<String, Map> getMotifsAProcess(Map<String, Map> motifSet){
          
     String output=null;
     OutputStream stdin = null;
     InputStream stderr = null;
     InputStream stdout = null;
        
     Map<String, Map> motifMap = new HashMap<String, Map>();
     
      try{
          // launch EXE and grab stdin/stdout and stderr
        Process process = Runtime.getRuntime ().exec ("labelg.exe -i3 -I1:100\n");
     // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
        stdin = process.getOutputStream ();
        stderr = process.getErrorStream ();
        stdout = process.getInputStream ();
       
        Iterator iter = motifSet.keySet().iterator() ; 
        List<Map> countList = new ArrayList<Map>();
        while (iter.hasNext())  { 
            String skey = (String) iter.next();
             //System.out.println("skey = " + skey);
            Map vSet = motifSet.get(skey);
            
            //System.out.println("vSet = " + vSet);
            countList.add(vSet);
            
           // System.out.println("countList = " + countList);
            // "write" the parms into stdin
            String input =skey + "\n";   
            stdin.write(input.getBytes() );
            stdin.flush();
         }
        stdin.close();
        String line;
       // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
         iter = countList.iterator() ; 
        while ((line = brCleanUp.readLine ()) != null) {
        //System.out.println ("[Stdout] " + line);
            output = line;
            
            Map count = (Map) iter.next();
         if (!motifMap.containsKey(output)) motifMap.put(output, count); 
         else{
                Map tmp = motifMap.get(output);
                for(Object tkey: count.keySet()){
                        
                    if(tmp.containsKey(tkey)) {
                        Set tSet = (Set)tmp.get(tkey);
                        tSet.addAll((Set) count.get(tkey));
                         tmp.put(tkey, tSet);
                    }
                    else tmp.put(tkey, (Set)count.get(tkey));
                }
              motifMap.put(output, tmp);
         } 
      }
      brCleanUp.close();
    
      // clean up if any output in stderr
      brCleanUp = new BufferedReader (new InputStreamReader (stderr));
      while ((line = brCleanUp.readLine ()) != null) {
       // System.out.println ("[Stderr] " + line);
      }
      brCleanUp.close();
  
    }
    catch (Exception err) {
      err.printStackTrace();
    }
      
    
        
        return motifMap;
    }
      
     public static Map getMotifsAProcess(Map motifSet, boolean isClustering){
          
     String output=null;
     OutputStream stdin = null;
     InputStream stderr = null;
     InputStream stdout = null;
     
     Map motifMap = null;
     
         if (isClustering) {

             motifMap = new HashMap<String, Map>();

             try {
                 // launch EXE and grab stdin/stdout and stderr
                 Process process = Runtime.getRuntime().exec("labelg.exe -i3 -I1:100\n");
                 // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
                 stdin = process.getOutputStream();
                 stderr = process.getErrorStream();
                 stdout = process.getInputStream();

                 Iterator iter = motifSet.keySet().iterator();
                 List<Map> countList = new ArrayList<Map>();
                 while (iter.hasNext()) {
                     String skey = (String) iter.next();
                     //System.out.println("skey = " + skey);
                     Map vSet = (Map) motifSet.get(skey);

                     //System.out.println("vSet = " + vSet);
                     countList.add(vSet);

           // System.out.println("countList = " + countList);
                     // "write" the parms into stdin
                     String input = skey + "\n";
                     stdin.write(input.getBytes());
                     stdin.flush();
                 }
                 stdin.close();
                 String line;
                 // clean up if any output in stdout
                 BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
                 iter = countList.iterator();
                 while ((line = brCleanUp.readLine()) != null) {
                     //System.out.println ("[Stdout] " + line);
                     output = line;

                     Map count = (Map) iter.next();
                     if (!motifMap.containsKey(output)) {
                         motifMap.put(output, count);
                     } else {
                         Map tmp = (Map) motifMap.get(output);
                         for (Object tkey : count.keySet()) {

                             if (tmp.containsKey(tkey)) {
                                 Set tSet = (Set) tmp.get(tkey);
                                 tSet.addAll((Set) count.get(tkey));
                                 tmp.put(tkey, tSet);
                             } else {
                                 tmp.put(tkey, (Set) count.get(tkey));
                             }
                         }
                         motifMap.put(output, tmp);
                     }
                 }
                 brCleanUp.close();

                 // clean up if any output in stderr
                 brCleanUp = new BufferedReader(new InputStreamReader(stderr));
                 while ((line = brCleanUp.readLine()) != null) {
                     // System.out.println ("[Stderr] " + line);
                 }
                 brCleanUp.close();

             } catch (Exception err) {
                 err.printStackTrace();
             }

         }
         else{
             
             motifMap = new HashMap<String, Set>();

             try {
                 // launch EXE and grab stdin/stdout and stderr
                 Process process = Runtime.getRuntime().exec("labelg.exe -i3 -I1:100\n");
                 // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
                 stdin = process.getOutputStream();
                 stderr = process.getErrorStream();
                 stdout = process.getInputStream();

                 Iterator iter = motifSet.keySet().iterator();               
             
                 List<Set> countList = new ArrayList<Set>();
                 while (iter.hasNext()) {
                     String skey = (String) iter.next();
                     //System.out.println("skey = " + skey);
                     Set vSet = (Set) motifSet.get(skey);

                     //System.out.println("vSet = " + vSet);
                     countList.add(vSet);

           // System.out.println("countList = " + countList);
                     // "write" the parms into stdin
                     String input = skey + "\n";
                     stdin.write(input.getBytes());
                     stdin.flush();
                 }
                 
                 stdin.close();
                 String line;
                 // clean up if any output in stdout
                 BufferedReader brCleanUp = new BufferedReader(new InputStreamReader(stdout));
                 
                 iter = countList.iterator();
                 
                 while ((line = brCleanUp.readLine()) != null) {
                     //System.out.println ("[Stdout] " + line);
                     output = line;

                     Set count = (Set) iter.next();
                     if (!motifMap.containsKey(output)) {
                         motifMap.put(output, count);
                     } else {
                         Set tmp = (Set) motifMap.get(output);
                         tmp.addAll(count);
                         
                         motifMap.put(output, tmp);
                     }
                 }
                 brCleanUp.close();

                 // clean up if any output in stderr
                 brCleanUp = new BufferedReader(new InputStreamReader(stderr));
                 while ((line = brCleanUp.readLine()) != null) {
                     // System.out.println ("[Stderr] " + line);
                 }
                 brCleanUp.close();

             } catch (Exception err) {
                 err.printStackTrace();
             }
             
         }
        
        return motifMap;
    }
      
    public static Map<String, Integer> getSubgraphsProcess(Map<String, Integer> motifSet){
          
     String output=null;
     OutputStream stdin = null;
     InputStream stderr = null;
     InputStream stdout = null;
        
     Map<String, Integer> motifMap = new HashMap<String, Integer>();
     
      try{
          // launch EXE and grab stdin/stdout and stderr
        Process process = Runtime.getRuntime ().exec ("labelg.exe -i3 -I1:100\n");
     // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
        stdin = process.getOutputStream ();
        stderr = process.getErrorStream ();
        stdout = process.getInputStream ();
       
        Iterator iter = motifSet.keySet().iterator() ; 
        List<Integer> countList = new ArrayList<Integer>();
        while (iter.hasNext())  { 
            String skey = (String) iter.next();
             //System.out.println("skey = " + skey);
            Integer vSet = motifSet.get(skey);
            
            //System.out.println("vSet = " + vSet);
            countList.add(vSet);
            
           // System.out.println("countList = " + countList);
            // "write" the parms into stdin
            String input =skey + "\n";   
            stdin.write(input.getBytes() );
            stdin.flush();
         }
        stdin.close();
        String line;
       // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
         iter = countList.iterator() ; 
        while ((line = brCleanUp.readLine ()) != null) {
        //System.out.println ("[Stdout] " + line);
            output = line;
            
            Integer count = (Integer) iter.next();
         if (!motifMap.containsKey(output)) motifMap.put(output, count); 
         else{
              motifMap.put(output, motifMap.get(output)+count);
         } 
      }
      brCleanUp.close();
    
      // clean up if any output in stderr
      brCleanUp = new BufferedReader (new InputStreamReader (stderr));
      while ((line = brCleanUp.readLine ()) != null) {
       // System.out.println ("[Stderr] " + line);
      }
      brCleanUp.close();
  
    }
    catch (Exception err) {
      err.printStackTrace();
    }
      
    
        
        return motifMap;
    }
      
     // This applies for RAND_ESU with Pd and motifsize
        
   public static Map<String, Integer> getSubgraphsProcess(Map<String, Integer> motifSet, double Pd, int k){
          
     String output=null;
     OutputStream stdin = null;
     InputStream stderr = null;
     InputStream stdout = null;
        
     Map<String, Integer> motifMap = new HashMap<String, Integer>();
     
      try{
          // launch EXE and grab stdin/stdout and stderr
        Process process = Runtime.getRuntime ().exec ("labelg.exe -i3 -I1:100\n");
     // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
        stdin = process.getOutputStream ();
        stderr = process.getErrorStream ();
        stdout = process.getInputStream ();
       
        Iterator iter = motifSet.keySet().iterator() ; 
        List<Integer> countList = new ArrayList<Integer>();
        while (iter.hasNext())  { 
            String skey = (String) iter.next();
             //System.out.println("skey = " + skey);
            Integer vSet = motifSet.get(skey);
            float pk = (float) Math.pow(Pd, k);
            vSet = Math.round(vSet/pk);
            
            //System.out.println("vSet = " + vSet);
            countList.add(vSet);
            
           // System.out.println("countList = " + countList);
            // "write" the parms into stdin
            String input =skey + "\n";   
            stdin.write(input.getBytes() );
            stdin.flush();
         }
        stdin.close();
        String line;
       // clean up if any output in stdout
        BufferedReader brCleanUp = new BufferedReader (new InputStreamReader (stdout));
         iter = countList.iterator() ; 
        while ((line = brCleanUp.readLine ()) != null) {
        //System.out.println ("[Stdout] " + line);
            output = line;
            
            Integer count = (Integer) iter.next();
         if (!motifMap.containsKey(output)) motifMap.put(output, count); 
         else{
              motifMap.put(output, motifMap.get(output)+count);
         } 
      }
      brCleanUp.close();
    
      // clean up if any output in stderr
      brCleanUp = new BufferedReader (new InputStreamReader (stderr));
      while ((line = brCleanUp.readLine ()) != null) {
       // System.out.println ("[Stderr] " + line);
      }
      brCleanUp.close();
  
    }
    catch (Exception err) {
      err.printStackTrace();
    }
      
    
        
        return motifMap;
    }
        
      
public static String getLabel(DoubleMatrix2D matrix){
    
    String gh6 = matTOg6(matrix);
   // System.out.println("G6 format = " + gh6);
    
    String output=null;
    OutputStream stdin = null;
    InputStream stderr = null;
    InputStream stdout = null;
    
    try{

      // launch EXE and grab stdin/stdout and stderr
      Process process = Runtime.getRuntime ().exec ("labelg.exe -i3 -I1:100\n");
     // Process process = Runtime.getRuntime ().exec ("labelg.exe -i15\n");
      stdin = process.getOutputStream ();
      stderr = process.getErrorStream ();
      stdout = process.getInputStream ();
  
      // "write" the parms into stdin
      String input =gh6 + "\n";   
      stdin.write(input.getBytes() );
      stdin.flush();

      stdin.close();
        String line;
       // clean up if any output in stdout
      BufferedReader brCleanUp = 
        new BufferedReader (new InputStreamReader (stdout));
      while ((line = brCleanUp.readLine ()) != null) {
        //System.out.println ("[Stdout] " + line);
        output = line;
        
      }
      brCleanUp.close();
    
      // clean up if any output in stderr
      brCleanUp = new BufferedReader (new InputStreamReader (stderr));
      while ((line = brCleanUp.readLine ()) != null) {
       // System.out.println ("[Stderr] " + line);
       
      }
      brCleanUp.close();
  
    }
    catch (Exception err) {
      err.printStackTrace();
    }
    

    return output;
    
}

public static String matTOg6(DoubleMatrix2D matrix){

     int n=matrix.columns();
        /* Read upper triangle vertices in each column*/
        String binString="";
        for (int i=1; i<matrix.columns();i++) {
            DoubleMatrix2D col = matrix.viewPart(0, i, i, 1);
            double[][] dArray = col.toArray();
            
            for (int k=0; k<col.size();k++){

                binString = binString+(int)dArray[k][0];}
         }
        
        /* Make the binString to multiple of 6*/
        int multiInt = 6;
        binString = addPost2MultiInt(binString, multiInt);

        /* Now make R(x)*/
        int constAd = 63;
        String R=toRx(binString, multiInt, constAd);
       
         /* Now make N(n)*/
        String Nn = toNn(n);
        
       
    return  Nn+R;
}
        
public static String toNn(int n){
    String str = "";
    int multiInt = 6;
    int constAd = 63;

     if (n<0) return str;
        if (n < 63) str = str+(char)(n+63);
        else if (n<258047)  {
            String tmp=addPre2MultiInt(Integer.toBinaryString(n), multiInt);
            while ((int) tmp.length()/6 < 3) tmp = "000000" + tmp;
            str =  str+ (char)126 + toRx(tmp, multiInt, constAd);
        }
        else if (n<Integer.MAX_VALUE)  {
           String tmp=addPre2MultiInt(Integer.toBinaryString(n), multiInt);
            while ((int) tmp.length()/6 < 6) tmp = "000000" + tmp;
            str =  str+ (char)126 + (char)126+ toRx(tmp, multiInt, constAd);
        }
        else return str;

    return str;
}

public static String addPre2MultiInt(String str, int k){
    while(str.length()%k != 0){
        str = "0" + str;
     }
     System.out.println(str);
    return str;
}

public static String addPost2MultiInt(String str, int k){
     while(str.length()%k != 0){
        str = str + "0";
       }
    return str;
}

public static String toRx(String binString, int k, int ct){
    String R="";
        while (binString.length()>0){
            char tmp = (char) (Integer.parseInt(binString.substring(0, k),2) + ct);
            R = R+tmp;
            binString = binString.substring(6);
         }
    return R;
}

 
                 // Test for Graph labeling
public static void main(String args[]) { 
     Graph g = null;

        GraphLabel labeler = new GraphLabel();
        GraphFileReader reader = new GraphFileReader("../datafiles/GraphsTest-1.net");
        
        
        /* Read a graph*/
          try
                {
                  g = reader.fileRead(false);   
                }
                catch (Exception e)
                 {
                 System.out.println("Error in loading graph");
                 e.printStackTrace();
                 return;
                }
        
          GraphMatrixOperations GMO = new GraphMatrixOperations();
        //*Convert it to matrix format
        int n=g.getVertexCount();
        
         SparseDoubleMatrix2D fullmatrix = GMO.graphToSparseMatrix(g);
        
        
        String label = labeler.getLabel(fullmatrix);
        System.out.println("Label = " + label);
        return;  
    }

}

