package edu.uw.nemo.demo;


import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.*;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.*;
import cern.colt.matrix.DoubleMatrix2D;
import edu.uci.ics.jung.algorithms.matrix.*;
import edu.uci.ics.jung.algorithms.util.Indexer;


import org.apache.commons.collections15.BidiMap;

/**
 *
 * @author Wooyoung
 * isClustering is true in default, meaning it uses graph clustering before
 * motif search.
 */
public class MotifCount{
    
    private Graph g;
    private int motifSize;
   
    BidiMap indexer;
    //private Set vertexListSet;
    private String clusterLabel;
    private Map MotifSet;
    private double pd;
    private int nSearch;
    
    // added later (3/25/14)
    private boolean isClustering = true;
    private boolean isRemoving = false;
    private Map removeSet;
    private Graph oldg = null;

    
    //Constructor
   
    // Without clustering, when we try to count motifs.
    // This will call EnumerateSubgraph
    // Only one vertex list
     public MotifCount(Graph gr, int k, Map motifSet){
        g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = Indexer.create(g.getVertices());
        clusterLabel = "NOCLUSTER";
        MotifSet = motifSet;
        pd = 1;
     }
     
      public MotifCount(Graph gr, int k, Map motifSet, double p){
        g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = Indexer.create(g.getVertices());
        clusterLabel = "NOCLUSTER";
        MotifSet = motifSet;
        pd = p;
     }
      
      public MotifCount(Graph gr, int k, Map motifSet, int num){
        g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = Indexer.create(g.getVertices());
        clusterLabel = "NOCLUSTER";
        MotifSet = motifSet;
        nSearch = num;
        pd = 1;
     }
        
      public MotifCount(Graph gr, int k, Map motifSet, double p, String cLabel){
        g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = Indexer.create(g.getVertices());
        clusterLabel = "NOCLUSTER";
        MotifSet = motifSet;
        pd = p;
        clusterLabel = cLabel;
     }
    
  
 
    //After we get a number of removed edges, we cluster which will only cound motifs from removed edges
     // This class will call EnumerateSubEdge
      public MotifCount(Graph gr, int k, Map motifSet, String cLabel){
       
    
        g = gr;
        motifSize = k;
        /*Convert it to matrix format*/

        indexer = Indexer.create(g.getVertices());
        
        MotifSet = motifSet;
        clusterLabel = cLabel;
        pd = 1;
       
     }
    
       public MotifCount(Graph gr, int k, Map motifSet, BidiMap index){
        g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = index;
      //  System.out.println("In Motif " + indexer);  
        MotifSet = motifSet;
        pd = 1;
     }
       
     public MotifCount(Graph gr, int k, Map motifSet, BidiMap index, boolean isCluster){
        /* This construct is for existing BidiMap and can use the option for isCluster
            if isCluster is true then using clustering options. Otherwise
            use just a plain motifset*/
            
            g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = index;
      //  System.out.println("In Motif " + indexer);  
        MotifSet = motifSet;
        pd = 1;
        isClustering = isCluster;
     }
     
     
     public MotifCount(Graph gr, int k, Map motifSet, Map remSet, 
             BidiMap index, boolean isCluster, boolean isRemove, Graph oldgraph){
        /* This construct is for existing BidiMap and can use the option for isCluster
            if isCluster is true then using clustering options. Otherwise
            use just a plain motifset*/
            
            g = gr;
        motifSize = k;
        /*Convert it to matrix format*/
        //fullmat = GraphMatrixOperations.graphToSparseMatrix(g);
 
        indexer = index;
      //  System.out.println("In Motif " + indexer);  
        MotifSet = motifSet;
        pd = 1;
        isClustering = isCluster;
        removeSet = remSet;
        isRemoving = isRemove;
        oldg = oldgraph;
     }
     
     
     
    // count the number of motifs and update the existing MotifSet
   
    public void EnumerateSubgraps(){
        
       // for each vertex v in V do Ve={u in neighbor v where u > v
        
        Collection vertices = g.getVertices();
        if (vertices == null)
        {
           System.out.println("ERROR: No vertices");
            return;
        }
        for (Object v: vertices){
            if( Math.random()<=pd) {
                /////ve <= neigbhor of v
               // System.out.println("Vertex =" + v);
                Set Nv = new HashSet();
                for(Object u:g.getNeighbors(v)){
                    if(indexer.get(u).hashCode()> indexer.get(v).hashCode()) Nv.add(u);
                }
              //  System.out.println("neighbor=" + Nv);

                Collection sv = new HashSet();
                sv.add(v);


                Graph Vs = FilterUtils.createInducedSubgraph(sv, g);
               // System.out.println("Vs=" + Vs);
               //  System.out.println("\t  Nv = " + Nv + "  v=" + v);
                ExtendSubgraph(Vs, Nv, v);
            }
        }
    }
    // Implementation of MFinder sampling algorithm
    public void SampleSubgraps(){
        
        List Edges = new ArrayList(g.getEdges());
        Set VsSet = new HashSet();
        
        
        
        RAND: while (VsSet.size()<nSearch){
            
           int item = new Random().nextInt(Edges.size());
            Object e = Edges.get(item);
            
        
       // for each vertex v in V do Ve={u in neighbor v where u > v
        
       

                Set Es = new HashSet();
                Es.add(e);
                Set Vs = new HashSet();
                Vs.add(g.getEndpoints(e).getFirst());
                Vs.add(g.getEndpoints(e).getSecond());
                Graph Gs = FilterUtils.createInducedSubgraph(Vs, g);

                 if(Vs.size() == motifSize) {
                     if(!VsSet.contains(Vs)){
                     
                     VsSet.add(Vs);

                     addSubGraphs(Gs);
                     }
                     continue RAND;
                 }


                while(Vs.size()<motifSize){
                    Set Lset = getNeighborEdges(Es);
                    Lset.removeAll(Es);
                    if(Lset.size()<1) continue RAND;
                     List L = new ArrayList();
                    for(Object newE: Lset){
                        if (!Gs.getEdges().contains(newE)) {
                            //System.out.println("adding edge = " + newE);

                            L.add(newE);
                        }
                    }

                    if(L.size()<1) continue RAND;

                    int rditem = new Random().nextInt(L.size());
                    Object enew = L.get(rditem);
                    Es.add(enew);
                    Vs.add(g.getEndpoints(enew).getFirst());
                    Vs.add(g.getEndpoints(enew).getSecond());
                    Gs = FilterUtils.createInducedSubgraph(Vs, g);
                     if(Vs.size() == motifSize) break;

                }

                Gs = FilterUtils.createInducedSubgraph(Vs, g);
                if(!VsSet.contains(Vs)){
                VsSet.add(Vs);
                addSubGraphs(Gs);
                }
                
                if(VsSet.size()%100==0) System.out.println("Search = " + VsSet.size());




            }

      
    }
    
    /* public void SampleSubgraps(){
        
       // for each vertex v in V do Ve={u in neighbor v where u > v
        
        RAND:for(Object e:g.getEdges()){
            System.out.println("edge = " + e);
            
            Set Es = new HashSet();
            Es.add(e);
            Set Vs = new HashSet();
            Vs.add(g.getEndpoints(e).getFirst());
            Vs.add(g.getEndpoints(e).getSecond());
            Graph Gs = FilterUtils.createInducedSubgraph(Vs, g);
            
             if(Vs.size() == motifSize) {
                 
                 addSubGraphs(Gs);
                 continue RAND;
             }
       
            
            while(Vs.size()<motifSize){
            Set Lset = getNeighborEdges(Es);
            Lset.removeAll(Es);
            if(Lset.size()<1) continue RAND;
             List L = new ArrayList();
            for(Object newE: Lset){
                if (!Gs.getEdges().contains(newE)) {
                    System.out.println("adding edge = " + newE);
                            
                    L.add(newE);
                }
            }
           
            if(L.size()<1) continue;
           
            int rditem = new Random().nextInt(L.size());
            Object enew = L.get(rditem);
            Es.add(enew);
            Vs.add(g.getEndpoints(enew).getFirst());
            Vs.add(g.getEndpoints(enew).getSecond());
            Gs = FilterUtils.createInducedSubgraph(Vs, g);
             if(Vs.size() == motifSize) break;
            
            }
            
            Gs = FilterUtils.createInducedSubgraph(Vs, g);
            addSubGraphs(Gs);
                  
            
           
            
        }
        
      
    }*/
        
        
       //System.out.println("After return MotifSet = " + MotifSet);
          
        
         //System.out.println(searchedV+ "  vertices searched ");
    
     
    
    public void ExtendSubgraph(Graph Vs, Set Vextend, Object v ){
        
        //System.out.println("VS=" + Vs + "  Vextend = " + Vextend + "  v=" + v);
        
        if(Vs.getVertexCount() == motifSize) {
           
             addSubGraphs(Vs);
            return;
        }
        
        
        Collection NVs = new HashSet();
        for(Object x:Vs.getVertices()){
            NVs.add(x);
            NVs.addAll(g.getNeighbors(x));
        }
       
        while(!Vextend.isEmpty()){
            // Remove an arbitrarily chosen vertex w from Vetenstion (actuall from the first index)
           
           
            Object w = Vextend.iterator().next();

            Vextend.remove(w);
            
            if(Math.random()<=pd){
            Collection newVs = new HashSet();
            newVs.addAll(Vs.getVertices());
            newVs.add(w);
         
            Graph nSub = FilterUtils.createInducedSubgraph(newVs, g);
            
            if(nSub.getVertexCount() == motifSize){
                 addSubGraphs(nSub);
                 continue;
            }
            
             Collection neghW = g.getNeighbors(w);
  
            Set Vextp = new HashSet();
            Vextp.addAll(Vextend);
            for(Object z: neghW){
                     
                if(indexer.get(z).hashCode()>indexer.get(v).hashCode())
                    if(!NVs.contains(z)) Vextp.add(z);
            }
            ExtendSubgraph(nSub, Vextp, v);           
           
        }
    }
        return;
    }
    
    
    public void ExtendSubgraph2(Graph Vs, Set Vextend, Object v ){
        
        //System.out.println("VS=" + Vs + "  Vextend = " + Vextend + "  v=" + v);
        
        if(Vs.getVertexCount() == motifSize) {
           
             addSubGraphs(Vs);
            return;
        }
        
        
        Collection NVs = new HashSet();
        for(Object x:Vs.getVertices()){
            NVs.add(x);
            NVs.addAll(g.getNeighbors(x));
        }
       
        while(!Vextend.isEmpty()){
            // Remove an arbitrarily chosen vertex w from Vetenstion (actuall from the first index)
           
           
            Object w = Vextend.iterator().next();

            Vextend.remove(w);
            
            if(Math.random()<=pd){
            Collection newVs = new HashSet();
            newVs.addAll(Vs.getVertices());
            newVs.add(w);
         
            Graph nSub = FilterUtils.createInducedSubgraph(newVs, g);
            
            if(nSub.getVertexCount() == motifSize){
                 addSubGraphs(nSub);
                 continue;
            }
            
             Collection neghW = g.getNeighbors(w);
  
            Set Vextp = new HashSet();
            Vextp.addAll(Vextend);
            for(Object z: neghW){
                     
               // if(indexer.get(z).hashCode()>indexer.get(v).hashCode())
                    if(!NVs.contains(z)) Vextp.add(z);
            }
            ExtendSubgraph2(nSub, Vextp, v);           
           
        }
    }
        return;
    }
    
    public void addSubGraphs(Graph gr) {

        // convert the graph to matrix format to get the label
        DoubleMatrix2D submat = GraphMatrixOperations.graphToSparseMatrix(gr);
        String label = GraphLabel.matTOg6(submat);
       // System.out.println("add to subgraph" + gr.getVertices());
        // put their indices instead of vertex just to save the space.
        List vertices = new ArrayList();
        vertices.addAll(gr.getVertices());
        Collections.sort(vertices);
        if (isClustering) {
            Map<String, Set> idMap;
            Set idSet;

            if (!MotifSet.containsKey(label)) {
                idMap = new HashMap<String, Set>();
                idSet = new HashSet();
            } else {
                idMap = (Map) MotifSet.get(label);
                if (!idMap.containsKey(clusterLabel)) {
                    idSet = new HashSet();
                } else {
                    idSet = idMap.get(clusterLabel);
                }

            }

            idSet.add(vertices);
            idMap.put(clusterLabel, idSet);

            MotifSet.put(label, idMap);
        } /* Added in case of isClustering is false. 3/25/2014*/ 
        else {
            Set idSet;
            if (!MotifSet.containsKey(label)) {
                idSet = new HashSet();
            } else {
                idSet = (Set) MotifSet.get(label);
            }
            idSet.add(vertices);
            MotifSet.put(label, idSet);

            if (isRemoving) {
                /*If isRemoving is true, then make another induced subgraph
                 from the old graph to get different label and different 
                 connectivity*/
                Graph subOld = FilterUtils.createInducedSubgraph(gr.getVertices(), oldg);
                if (SMUNutil.nConnectedComponents(subOld) == 1) {

                //    System.out.println("add to removed set" + gr.getVertices());

                    String newlabel = GraphLabel.matTOg6(
                            GraphMatrixOperations.graphToSparseMatrix(
                                    subOld));

                    if (!removeSet.containsKey(newlabel)) {
                        idSet = new HashSet();
                    } else {
                        idSet = (Set) removeSet.get(newlabel);
                    }
                     idSet.add(vertices);

                    removeSet.put(newlabel, idSet);
                }

            }

        }

    }

    public void UnpdateSubgraphInLNE(Set LNE) {
        /* Added function in 3/26/2014, to extend subgraph from
         the added edge set. And will collect the repeated subgraph from 
         the old_graph
         */
            
        for (Object e : LNE) {
 
            Set ExtendV = new HashSet();
            Object first = g.getEndpoints(e).getFirst();
            Object second = g.getEndpoints(e).getSecond();
            ExtendV.addAll(g.getNeighbors(first));
            ExtendV.addAll(g.getNeighbors(second));

            Set Extend = new HashSet();
            for (Object v : ExtendV) {
                if (v != first && v != second) {
                        Extend.add(v);
 
                }

            }
            
        //    System.out.println("Extend=" + Extend);


            Graph Vs = FilterUtils.createInducedSubgraph(g.getEndpoints(e), g);
               // System.out.println("Vs=" + Vs);
            //  System.out.println("\t  Nv = " + Nv + "  v=" + v);
            ExtendSubgraph2(Vs, Extend, first);

        }

 //        System.out.println(searchedV+ "  vertices searched ");
    }

    public void EnumerateSubEdge(Set removed_edge) {

        Set curr_removed_edges = new HashSet();
          //Set<Number> prev_removed_edges = new TreeSet<Number>();

        for (Object e : removed_edge) {
            curr_removed_edges.add(e);
            Set Eextend = getEextend(e, curr_removed_edges);

            Set Esub = new HashSet();
            Esub.add(e);
            // Pass the vertex info as well
            Set Vs = new HashSet();
            Vs.addAll(g.getEndpoints(e));

            Set Ngb_Esub = new HashSet();
            Ngb_Esub.addAll(getNeighborEdges(Esub));
            Ngb_Esub.addAll(Esub);

//             System.out.println("Root edge = " + e + " " +g.getEndpoints(e) + "    : Eextend = "+ Eextend);
            ExtendSubEdge(Esub, Vs, Eextend, e, curr_removed_edges, Ngb_Esub);

        }

 //        System.out.println(searchedV+ "  vertices searched ");
    }
    
     public Set getNeighborEdges(Set edges){
         Set neighbors = new HashSet();
         for(Object e:edges) {
             Pair ends = g.getEndpoints(e);
             Collection conns = g.getIncidentEdges(ends.getFirst()); neighbors.addAll(conns);
             conns = g.getIncidentEdges(ends.getSecond()); neighbors.addAll(conns);
         }
        // System.out.println("size = " + neighbors);
         return neighbors;
    }
    
   
  public Set getEextend(Object e, Set curr_removed_edges){
         
 
        Pair ends = g.getEndpoints(e); 
        Object first = ends.getFirst(); 
        Object second = ends.getSecond();
        
        //System.out.println("previoulsy removed edge list" + pre_remedge_list+ "  Vertices=" +curr_removed_edges );
        Set Eextend = new HashSet();
        Collection conns = g.getIncidentEdges(first); 
         //System.out.println("First = ");
        for(Object neWithFirst: conns){ 
            if(!curr_removed_edges.contains(neWithFirst)){
             
                    Eextend.add(neWithFirst);
                   
            }
        }
        
         //System.out.println("Second = ");
        conns = g.getIncidentEdges(second); 
        for(Object neWithSecond: conns){ 
            if(!curr_removed_edges.contains(neWithSecond)){
                    Eextend.add(neWithSecond);                
            }
        }
        
       
        return Eextend;
    }
  
        public void ExtendSubEdge( Set Esub, Set Vs, Set Eextend, Object rootEdge, 
               Set curr_removed_edges, Set Ngb_Esub){
            
            Set<String> vertices = new HashSet<String>();
            //vertices = Vs;
            for(Object e:Esub) {
               vertices.addAll(g.getEndpoints(e));
            }
            
           
        Set<String> Vs_new = new HashSet<String>(); Vs_new.addAll(Vs); 
     
                       
         Label: 
         while(!Eextend.isEmpty())
          { 
              Iterator  wIt = Eextend.iterator(); 
              Object w = wIt.next(); wIt.remove();
              
             // System.out.println("Consider w: " +w );
              
              if (Vs_new.containsAll(g.getEndpoints(w))) {
                 // System.out.println(w + "   not a new vertex added to Vs_new: " + Vs_new );
                  continue Label;} // Newly added w produce duplicated vertices, back to while
              else Vs_new.addAll(g.getEndpoints(w));
              
              //Newely added w produces an edge in prev_removed_edges excluding rootedge, then back to while.
              Set subvertices = new HashSet<String>();
              subvertices.addAll(vertices); subvertices.addAll(g.getEndpoints(w));
              Graph sub = FilterUtils.createInducedSubgraph(subvertices, g);
              Collection subedges = sub.getEdges();
              
              for(Object tmpe:subedges) {
                  if(tmpe!=rootEdge){
                    if(curr_removed_edges.contains(tmpe)) {
                        //System.out.println(w + " causes the revisit with  : " + tmpe);
                      subvertices.clear(); continue Label;
                    }
                  }
                  
              }
              
              //System.out.println(w + " will be added to Esub : " + Esub);
              
              if(subvertices.size()==motifSize){
                  //Graph nSub = FilterUtils.createInducedSubgraph(subvertices, g);
                  
                  
                  addSubGraphs(sub);
               //   System.out.println(w + " will be added to Esub : " + Esub);
                  continue Label;
              }
              
              //* Vs_new is the union of Vs and {w}.
              Set Es_new = new HashSet(); 
              Es_new.addAll(Esub);  Es_new.add(w); 
              
                  
               Set N_ew = new HashSet();
              
              Set Ee_new = new HashSet();
              
              N_ew = getEextend(w, curr_removed_edges);
              
              N_ew.removeAll(Ngb_Esub);// N_ew.removeAll(Esub);
              
               //* Ee_new is the union of Ve and N_ew.
              
              Ee_new.addAll(Eextend); Ee_new.addAll(N_ew); 
             //  System.out.println("\t root edge = " + rootEdge+ "  Es_new = " + Es_new + "    : Ee_new = "+ Ee_new);
        // }
              Set ne_Es_new = new HashSet();
              ne_Es_new.addAll(Ngb_Esub);
              
             Pair ends = g.getEndpoints(w);
             Collection conns = g.getIncidentEdges(ends.getFirst()); ne_Es_new.addAll(conns);
             conns = g.getIncidentEdges(ends.getSecond()); ne_Es_new.addAll(conns);
             
             // System.out.println("Es_new = " + Es_new + "    : Ee_new = "+ Ee_new);
        
              ExtendSubEdge(Es_new, Vs_new, Ee_new, rootEdge, curr_removed_edges, ne_Es_new);
         }
        
    }
    
    
    
    
    public Map getMotifset(){
       return MotifSet;
   }
    
    
    
    /**************************************************************************************************/
    // RAND_ESU with p_d
    // This is only for counting subgraphs from generated graph
    // For simplicity, all Pd = is same for all depth
    
    
 
  
     
     public static void main(String[] args) {
        
        Graph g=null;

        int k=3;
        
        
        //GraphFileReader reader = new GraphFileReader("coli1_1_transcription.txt");
        
        GraphFileReader reader = new GraphFileReader("test_input_add.txt");
        try{
            g = reader.fileRead(false); 
        }
        catch (Exception e){
            System.out.println("Error in loading graph");
            e.printStackTrace();
        }
        
        System.out.println("Graph = " + g.getVertexCount()+"  Edge count  "+g.getEdgeCount());        
        // Start motif count
        
         Map<String, Map> motifSet = new HashMap<String, Map>();
         
          MotifCount counter = null;
          counter = new MotifCount(g, k, motifSet, 1);
       
  
         
         counter.EnumerateSubgraps();
         motifSet = counter.getMotifset();
         for(Object label: motifSet.keySet()){
             System.out.println(label + " : " + motifSet.get(label));
         }
         
              
         
         Map<String, Map> motifMap;
        
        System.out.println("motifset size = " + motifSet.size());
        motifMap = GraphLabel.getMotifsAProcess(motifSet);

        // print out motif sets

        Map<String, Integer> subgraphMap = new HashMap<String, Integer>();


        for (Iterator mIt = motifMap.keySet().iterator(); mIt.hasNext();) {
            Object akey = mIt.next();

            Map<String, Set> avalue = motifMap.get(akey);

            Integer count = 0;
            for (String st : avalue.keySet()) {

                int repeat = 1;
                if (st != null & !st.equalsIgnoreCase("EDGE")) {

                }
                count = count + (avalue.get(st).size()) * repeat;
            }


            subgraphMap.put((String) akey, count);
        }
        
         for(Object label: motifMap.keySet()){
             System.out.println(label + " : " + motifMap.get(label));
         }
         
         //System.out.println(motifSet + "   : Time = " + (endTime-startTime)/1000000000.0 + " seconds" + "\n motifset=" + motifSet.size());
     
        return;
      }
      

}
