package edu.uw.nemo.motifSignificant.explicitMethod.SwitchingAlgorithm;

import edu.uw.nemo.model.AdjacencyMapping;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;

import java.util.*;

/**
 * Created by Zalak on 4/8/2015.
 */
public class ConvertDataStructure {
   Hashtable<Integer, HashSet<Integer>> convertToAdjacencyHashtable(Mapping inputGraph) {
        Hashtable<Integer, HashSet<Integer>> adjacentListTable = new Hashtable<Integer, HashSet<Integer>>();
        HashSet<Integer> adjSet = null;
        for (int i = 0; i < inputGraph.getNodeCount(); i++) {
            if (adjacentListTable.containsKey(i)) {
                adjSet = adjacentListTable.get(i);
            } else {
                adjSet = new HashSet<Integer>();
            }

            List<AdjacentVertexWithEdge> adjVertexList = inputGraph.getNeighbours(i);
            for (AdjacentVertexWithEdge e : adjVertexList) {
                if (adjSet != null) {
                    if (!adjSet.contains(e.getNodeId())) {
                        adjSet.add(e.getNodeId());

                    }
                }
            }
            adjacentListTable.put(i, adjSet);
        }

        return adjacentListTable;
    }

    /**
     *
     * @param adjHashTable
     * @return
     */
    AdjacencyMapping convertToAdjacencyMapping(Hashtable<Integer, HashSet<Integer>> adjHashTable) {
        AdjacencyMapping map = new AdjacencyMapping(adjHashTable.size());
        for (Integer key : adjHashTable.keySet()) {
            HashSet<Integer> set = adjHashTable.get(key);
            for (Iterator iter = set.iterator(); iter.hasNext(); )
            {
                int adjNode = (Integer) iter.next();
                if (map.size() == 0)
                {
                    map.addAdjacentVertices(key, adjNode);
                    continue;
                }
                if (map.size() >= key)
                {
                    if (map.getNeighbour(key, adjNode) == null) {
                        map.addAdjacentVertices(key, adjNode);
                        continue;
                    }
                }
                if (map.size() >= adjNode)
                {
                    if (map.getNeighbour(adjNode, key) == null) {
                        map.addAdjacentVertices(adjNode, key);
                        continue;
                    }
                }

                if (!(map.size() >= key) || !(map.size() >= adjNode))
                    map.addAdjacentVertices(key, adjNode);

            }
        }

        return map;
    }

    /**
     *
     * @param adjList
     */
    void print(Hashtable<Integer, HashSet<Integer>> adjList) {
        for (Integer key : adjList.keySet()) {
            HashSet<Integer> set = adjList.get(key);
            System.out.print("\n" + key);
            for (Iterator iter = set.iterator(); iter.hasNext(); ) {
                System.out.print("- >" + iter.next());
            }
        }
    }

    /**
     *
     * @param map
     */
    void print(AdjacencyMapping map) {
        System.out.println("\n" + "--------------------------------------------------------------------------\n");
        for (int i = 0; i < map.size(); i++) {
            List<AdjacentVertexWithEdge> adjList = map.getNeighbours(i);
            System.out.print("\n" + i);
            for (AdjacentVertexWithEdge v : adjList) {
                System.out.print("->" + v.getNodeId());
            }
        }
    }

}



    /*
     public AdjacencyMapping convertToAdjacencyMapping(HashMap<Integer, ArrayList<Integer>> adjHashMap)
    {
        AdjacencyMapping map = new AdjacencyMapping(adjHashMap.size());
        for(Integer key: adjHashMap.keySet())
        {

            for(Iterator iter = adjHashMap.get(key).iterator(); iter.hasNext();)
            {
                int adjNode = (Integer)iter.next();
                if(map.size() == 0)
                {
                    map.addAdjacentVertices(key, adjNode);
                    continue;
                }
                if(map.size() >= key)
                {
                    if(map.getNeighbour(key, adjNode) == null)
                    {
                        map.addAdjacentVertices(key, adjNode);
                        continue;
                    }
                }
                if(map.size()>= adjNode)
                {
                    if(map.getNeighbour(adjNode, key) == null)
                    {
                        map.addAdjacentVertices(adjNode, key);
                        continue;
                    }
                }

                if(!(map.size() >=key) || !(map.size()>= adjNode))
                    map.addAdjacentVertices(key, adjNode);

            }
        }

        return map;
    }
    public void print(HashMap<Integer, ArrayList<Integer>> adjMap )
    {
        for(Integer key: adjMap.keySet())
        {
            ArrayList<Integer> list = adjMap.get(key);
            System.out.print("\n" + key);
            for(Iterator iter = list.iterator(); iter.hasNext();)
            {
                System.out.print("- >" + iter.next());
            }
        }


    }
     public HashMap<Integer, ArrayList<Integer>> convertToAdjacencyHashMap(Mapping inputGraph)
    {
        HashMap<Integer, ArrayList<Integer>> adjacentListMapping = new HashMap<Integer, ArrayList<Integer>> ();
        ArrayList<Integer> adjList = null;
        for(int i= 0; i< inputGraph.getNodeCount(); i++)
        {
            if(adjacentListMapping.containsKey(i))
            {
                adjList = adjacentListMapping.get(i);
            }
            else
            {
                adjList = new ArrayList<Integer>();
            }

            List<AdjacentVertexWithEdge> adjVertexList = inputGraph.getNeighbours(i);
            for(AdjacentVertexWithEdge e: adjVertexList )
            {
                if(adjList != null)
                {
                    if(!adjList.contains(e.getNodeId()))
                    {
                        adjList.add(e.getNodeId());
                    }
                }
            }
            adjacentListMapping.put(i, adjList);
        }

        return adjacentListMapping;
    }
     */


