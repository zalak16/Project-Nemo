package edu.uw.nemo.demo;

import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.algorithms.filters.FilterUtils;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.graph.util.Pair;
import java.util.HashSet;
import java.util.Set;

import edu.uci.ics.jung.algorithms.util.Indexer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import org.apache.commons.collections15.BidiMap;

/**
 *
 * @author wkim
 */
public class SMUNutil {

    public static Set getDiffVertices(Set input, Set ref){
        /* Get the list of different vertices, addedV = input - ref*/
        Set newV = new HashSet(input);
        newV.removeAll(ref);
        return newV;


    }

    public static Map getDegree(Set vertices, Graph g){

        Map degreeMap = new HashMap<Integer, Set>();

        for (Object v:vertices){
            Integer degree = g.getNeighborCount(v);
            Set verSet = null;
            if (degreeMap.keySet().contains(degree))
                verSet = (Set) degreeMap.get(degree);
            else verSet = new HashSet();
            verSet.add(v);
            degreeMap.put(degree, verSet);

        }
        return degreeMap;

    }

    public static int nConnectedComponents(Graph g) {

        WeakComponentClusterer clusterer = new WeakComponentClusterer();

        Set clusterSet = clusterer.transform(g);


        return clusterSet.size();
    }



    public static Set getDiffEdges(Graph input, Graph ref, boolean directed){
        /* Get the list of different edges, addedE = input - ref*/

        Collection addedE = new HashSet();

        Collection inputE, refE = new HashSet();
        if (directed) {
            inputE = input.getEdges(EdgeType.DIRECTED);
            refE = ref.getEdges(EdgeType.DIRECTED);
        }
        else {
            inputE =  input.getEdges(EdgeType.UNDIRECTED);
            refE =  ref.getEdges(EdgeType.UNDIRECTED);
        }
        addedE.addAll(inputE);


        for (Object v : inputE) {
            Pair inputPair = input.getEndpoints(v);
            for (Object w : refE) {
                Pair refPair = ref.getEndpoints(w);
                if(directed){
                    if (refPair.equals(inputPair))
                        addedE.remove(v);
                }
                else{
                    if (inputPair.containsAll(refPair))
                        addedE.remove(v);}

            }
        }

        return (Set) addedE;

    }

    public static Pair getListAddedEdges(Set<Pair> edgeSet, Set verSet){
        /* Get the pair of set. The first set is the edge set where
        all endpoints belong to the verSet, the second set is the edge where
        only one edge is from the verSet*/

        Set edge2Set = new HashSet();
        Set edge1Set = new HashSet();
        Set edge0Set = new HashSet();


        for (Pair v : edgeSet) {
            Object fv = v.getFirst();
            Object sv = v.getSecond();

            if(verSet.contains(fv)){
                edge1Set.add(v);
                if(verSet.contains(sv)) {
                    edge2Set.add(v);
                    edge1Set.remove(v);
                }

            }
            else if(verSet.contains(sv)){
                edge1Set.add(v);
            }
            else edge0Set.add(v);


        }


        //System.out.println("\n"+edge0Set.size() + " 0 Set = " + edge0Set.toString()+"\n");
        Pair<Set> result = new Pair(edge2Set, edge1Set);


        return result;


    }


    public static Set getVertexSet(Graph g){

        Set vertices = new HashSet();

        for (Object v : g.getVertices()) {
            vertices.add(v);
        }

        return vertices;

    }

    public static Set getLNE(Graph input, Graph ref, Set addV, boolean directed){

         /* Get the list of new added edge, where all endpoints were old*/
        //Set addE = getDiffEdges(input, ref);


        Set addE = getDiffEdges(input, ref, directed);

        Set LNE = new HashSet();
        for (Object e : addE) {
            Object fv = input.getEndpoints(e).getFirst();
            Object sv = input.getEndpoints(e).getSecond();

            if(!addV.contains(fv))
                if (!addV.contains(sv)) LNE.add(e);
        }
        //Set LNE = new HashSet();

        return LNE;


    }

    public static boolean updateIndex (BidiMap indexer, Set LNV){
         /* Add the lowever number of indices of the added vertices and update
         the current indexer so that it is indexer for the new graph*/

        BidiMap added_indexer =
                Indexer.create(LNV, new Integer((-1)*LNV.size()));

        for (Object v:LNV){
            Integer idx = added_indexer.get(v).hashCode();
            indexer.put(v, idx);
        }

        return true;



    }

    public static void printMotifs(Map motifMap){

        for(Object label: motifMap.keySet()){
            Set value = (Set)motifMap.get(label);
            System.out.println(label + " : " + value.size());
            for(Object idList:value){

                System.out.println(idList);
            }

        }

    }

    public static void printMotifsLNV(Map motifMap, Set LNV){

        for(Object label: motifMap.keySet()){
            Set<Set> value = (Set<Set>)motifMap.get(label);
            System.out.println(label + " : " + value.size());
            Set vertices = new HashSet(LNV);
            for(Set idList:value){
                vertices.retainAll(idList);
                if(!vertices.isEmpty()) System.out.println(idList);
            }

        }

    }

    public static void printMotifs2(Map motifMap, Set LNV){

        Set LNVvertices = new HashSet();
        for (Object v:LNV){
            LNVvertices.add(v);
        }

        for(Object label: motifMap.keySet()){
            Set value = (Set)motifMap.get(label);

            System.out.println(label + " : " + value.size());
            for(Object idList:value){

                Set intersection = new HashSet((Set)idList);
                intersection.retainAll(LNVvertices);

                if (!intersection.isEmpty())
                    System.out.println(": "+idList);
            }


        }

    }

    public static void printDiffMotifs(Map motifMap,Map newmotifMap, Set LNV){

        System.out.println("print different motifs");

        Map restMap = new HashMap();

        for(Object label: motifMap.keySet()){

            Set value = (Set)motifMap.get(label);
            Set newvalue = (Set)newmotifMap.get(label);

            System.out.println("label = " + label + "  original size = "
                    + value.size()+" : new size = " + newvalue.size());

            Set rest = new HashSet();
            for (Object aset: value){
                if(!newvalue.contains(aset)) rest.add(aset);
            }

            restMap.put(label, rest);



             /*for(Object idList:value){


                 Set intersection = new HashSet((Set)idList);
                 intersection.retainAll(LNVvertices);

                 if (!intersection.isEmpty()){
                     System.out.println("vertices from LNV ");

                 }
                 else System.out.println("Vertices from original");



             }*/




        }
        printMotifs(restMap);

    }

    public static void printHashMap(Map hashMap){
        System.out.println("Printing hash Map ");
        for (Object key:hashMap.keySet()){
            Set value = (Set) hashMap.get(key);

            System.out.println(key + ": size = " +value.size() +":"+value);

        }
    }

    public static void printMotifLabels(Map motifMap){

        for(Object label: motifMap.keySet()){
            Set value = (Set)motifMap.get(label);
            System.out.println(label + " : " + value.size());

        }

    }

    public static void printLNE(Set LNE, Graph g){

        for (Object e:LNE)
            System.out.println(g.getEndpoints(e));

    }

    public static void printEdges (Graph g){

        for (Object e:g.getEdges()){
            Pair pair = g.getEndpoints(e);
            System.out.println(e + " = " + pair);
        }

    }


    public static void main(String[] args) throws Exception{

        /** The main function is just a test function*/

        // Read a new graph (extended graph)
        boolean directed = false;
        GraphFileReader reader = new GraphFileReader("test_old.txt");
        //Scere20140117CR
        Graph newg=reader.fileRead(directed);
        System.out.println("new graph = " +
                newg.getVertexCount()+ "," + newg.getEdgeCount());



        // read an old graph file
        GraphFileReader refReader = new GraphFileReader ("Scere20131031CR.txt");
        //Scere20131031CR
        Graph oldg=refReader.fileRead(directed);
        System.out.println("old graph = " +
                oldg.getVertexCount()+ "," + oldg.getEdgeCount());


        // Get the list of new vertices LNV = newgraph-oldgraph

        Set curV = getVertexSet(newg);
        Set refV = getVertexSet(oldg);
        System.out.println("curV = " + curV.size());
        Set LNV = getDiffVertices(curV, refV);
        System.out.println("refV = " + refV.size());


        System.out.println("LNV size = "+ LNV.size() + "  : ");
        //for (Object v:LNV) System.out.println(v);

        //printHashMap(getDegree(LNV, g));

        // get the list of new edges from existing vertices
        Set LNE = getLNE(newg, oldg, LNV, directed);

        System.out.println("Size of LNE = " + LNE.size());
        //for (Object v:LNE) System.out.println(newg.getEndpoints(v));

        /* Search the motifs using this current indexer

        Update the indexer so that the new added vertices are lower labeled


        Motif count using no clustering option: First count motifs
        on the old graphs
        */




        int k=3;





        System.out.println("Old graph motifs");

        /*We will update indexer and motifset one by one */

        Map<String, Set> motifSet = new HashMap<String, Set>();

        BidiMap indexer = Indexer.create(refV, new Integer(0));


        MotifCount counter = new MotifCount(oldg, k, motifSet, indexer, false);

        long startTime = System.currentTimeMillis();
        counter.EnumerateSubgraps();

        long endTime   = System.currentTimeMillis();
        long totalTime = endTime - startTime;
        System.out.println("Time for the old graph = " +totalTime);


        Map<String, Set> motifMap = GraphLabel.getMotifsAProcess(motifSet, false);

        printMotifLabels(motifMap);
        //printMotifs(motifMap);


        System.out.println("New graph motifs: motif size = " + k);

        Map<String, Set> motifSetN = new HashMap<String, Set>();




        MotifCount counterN = new MotifCount(newg, k, motifSetN,
                Indexer.create(newg.getVertices()), false);

        startTime = System.currentTimeMillis();
        counterN.EnumerateSubgraps();

        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Time for the new graph = " +totalTime);


        Map<String, Set> motifMapN = GraphLabel.getMotifsAProcess(motifSetN, false);


        System.out.println("New graph motifs");
        printMotifLabels(motifMapN);
        //printMotifs(motifMapN);

        System.out.println("From now on, we will update the motifs from the "
                + "old graph");

        //test how many after adding LNV: have gLNV copy from g. Then
        //remove the edges in LNE
        // Make the copy of new graph as  gLNV, and remove LNE--> same
        // as removing LNE from the original graph
        System.out.println("Update motifs with LNV");
        Graph gLNV=reader.fileRead(directed);

        for(Object v: LNE){

            gLNV.removeEdge(v);

        }

        updateIndex(indexer, LNV); // update indexes with LNV

        // in this counter, we use the existing motifSet
        MotifCount counterEx = new MotifCount(gLNV, k, motifSet, indexer, false);

        startTime = System.currentTimeMillis();

        for (Object v : LNV) {

            Set Nv = new HashSet();
            for (Object u : gLNV.getNeighbors(v)) {
                if (indexer.get(u).hashCode() > indexer.get(v).hashCode())
                    Nv.add(u);

            }

            Collection sv = new HashSet();
            sv.add(v);

            Graph Vs = FilterUtils.createInducedSubgraph(sv, gLNV);
            counterEx.ExtendSubgraph(Vs, Nv, v);

        }



        System.out.println("Extended motif map final");

            /* Now add the subgraphs from LNE*/

        Map<String, Set> removeSet = new HashMap<String, Set>();

        MotifCount counterLNE = new MotifCount(newg, k, motifSet, removeSet,
                indexer, false, true, gLNV);

        counterLNE.UnpdateSubgraphInLNE(LNE);

        endTime   = System.currentTimeMillis();
        totalTime = endTime - startTime;
        System.out.println("Time for the update = " +totalTime);

        Map<String, Set> motifMapAdded = GraphLabel.getMotifsAProcess(motifSet, false);




        //printMotifLabels(motifMapAdded);
        //printMotifs(motifMapAdded);
        // Printout the results


        //System.out.println("removed motif map");
        Map<String, Set> removeMapLNE = GraphLabel.getMotifsAProcess(removeSet, false);


        // Printout the results
        //printMotifLabels(removeMapLNE);
        //printMotifs(removeMapLNE);

        Map<String, Set> finalMap = new HashMap(motifMapAdded);
        for (String key:removeMapLNE.keySet()){
            Set setSubg = removeMapLNE.get(key);
            finalMap.get(key).removeAll(setSubg);

        }




        System.out.println("final motif map");

        printMotifLabels(finalMap);




        /*Now need to implement to remove the repeated counts : 3/26/2014*/

        // Add the remove set



        /*

       //Merge the motifMapEx to motifMap
        for (String label:motifMapEx.keySet()){
            Set exSet = motifMapEx.get(label);
            if(motifMap.containsKey(label)){
               exSet.addAll(motifMap.get(label));

            }
            motifMap.put(label, exSet);

        }

        // Printout the results
        for(Object label: motifMap.keySet()){
             Set<int[]> value = motifMap.get(label);
             System.out.println(label + " : " + motifMap.get(label).size());
             for(int[] idList:value){

                 for(int i=0;i<idList.length;i++)
                 {
                     System.out.print("["+i+"]"+indexer.getKey(idList[i])+" ");
                 }
                 System.out.println("");
             }

         }


        // print out extended motif sets


        Map removeSet = new HashMap<String, Set>();


         for(Object label: motifMap.keySet()){
             Set<int[]> value = motifMap.get(label);
             System.out.println(label + " : " + motifMap.get(label).size());
             for(int[] idList:value){

                 for(int i=0;i<idList.length;i++)
                 {
                     System.out.print("["+i+"]"+indexer.getKey(idList[i])+" ");
                 }
                 System.out.println("");
             }

         }







         // A test code to check if it is connected subgraph or not

          Collection sv = new HashSet();
          Graph Vs = FilterUtils.createInducedSubgraph(LNV, g);
          System.out.println("Number of connected components of LNV in g = " + nConnectedComponents(Vs));
                */

    }




}
