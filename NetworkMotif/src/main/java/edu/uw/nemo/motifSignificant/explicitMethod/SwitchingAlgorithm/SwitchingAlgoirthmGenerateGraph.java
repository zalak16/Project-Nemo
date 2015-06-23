package edu.uw.nemo.motifSignificant.explicitMethod.SwitchingAlgorithm;

import edu.uw.nemo.model.AdjacencyMapping;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;

import java.util.*;

/**
 * This class follows switching algorithm that preserves the degree sequence of original graph recommended by Kashtan
 * Created by Zalak on 4/8/2015.
 */
public class SwitchingAlgoirthmGenerateGraph
{
    Mapping inputGraph;
    public static final int Q = 1;

    /**
     * Assigning inputgraph mapping object to local mapping object of input graph
     * @param inputGraph
     */
    public SwitchingAlgoirthmGenerateGraph(Mapping inputGraph)
    {
        this.inputGraph = inputGraph;
    }

    /**
     * Empty constructor
     */
    public SwitchingAlgoirthmGenerateGraph()
    {
    }

    /**
     * Generate N random graphs following the switching algorithm
     * @param n : Number of random graphs to be generated
     * @return
     */
    public List<Mapping> generateGraph(int n)
    {
        List<Mapping> randomGraphList = new ArrayList<Mapping>();
        System.out.println("Input Graph");
        long start = System.currentTimeMillis();

        // generating 0 to  N-1 random graphs
        for(int i = 0; i<n; i++)
        {
            if(i == 0 || i == 100 || i == 500 || i == 400 || i ==800 || i == 1000)
            {
                long end = System.currentTimeMillis();
                System.out.println("Time taken to generate " + (i+1) + " random graphs is: " + (end - start));
            }
            System.out.println("\n Graph " + (i+1) + "\n");
            Mapping map = generateGraph();
            //Addingthe random graph mapping object to list
            randomGraphList.add(map);
        }

        return randomGraphList;
    }

    /**
     * Generate one random graph
     * @return
     */
    public Mapping generateGraph()
    {
        ConvertDataStructure convert = new ConvertDataStructure();

        //creating new random graph mapping object by copying the input graph value to random graph object.
        // Also, randomgraph mapping object is represented in Hashtable<Hashset> data structure for O(1) lookup time
        Hashtable<Integer, HashSet<Integer>> randomGraphAdjacenyList = convert.convertToAdjacencyHashtable(this.inputGraph);

        List<String[]> randomEdgeList = new ArrayList<String[]>();

        int []vertex_edge1;
        int [] vertex_edge2;
        long startTime = System.currentTimeMillis();

        //Swapping the edges for QE times where Q = 1 and E = number of edges in the input graph
        for(long i= 0; i< getQE(); i++)
        {
            //randomly selecting pair of edges
            vertex_edge1 = getToFromVertex();
            vertex_edge2 = getToFromVertex();

            //check the existence of edge1 in random graph mapping object
            //if edge1 exist the continue else find the next edge present in random graph mapping object
            if(!checkEdgeExistence(vertex_edge1, randomGraphAdjacenyList))
            {
                //edge1 does not exist; get the next edge
                vertex_edge1 = getNextEdge(vertex_edge1, randomGraphAdjacenyList);
                if(vertex_edge1[0] < 0 || vertex_edge1[1] < 0)
                {
                    continue;
                }
            }

            //check the existence of edge2 in random graph mapping object
            //if edge2 exist the continue else find the next edge present in random graph mapping object
            if(!checkEdgeExistence(vertex_edge2, randomGraphAdjacenyList))
            {
                //edge2 does not exist; get the next edge
                vertex_edge2 = getNextEdge(vertex_edge2, randomGraphAdjacenyList);
                if(vertex_edge2[0] < 0 || vertex_edge2[1] < 0)
                {
                    continue;
                }
            }

            //Check if all the four vertexes of both the edges are different or not
            //This check is done to avoid creation of self loop
            if(!isEdgeUnique(vertex_edge1, vertex_edge2))
            {
                i--;
                continue;
            }

            // If all the above condition is passed the swap the edges
            boolean flag = swap(vertex_edge1, vertex_edge2, randomGraphAdjacenyList, i);

            //If on swapping it is creating multiple edges in random graph mappong object then swapping fails.
            if(!flag)
            {
                i--;
                continue;
            }

            /*if(i % 10000 == 0)
            {
                long endTime = System.currentTimeMillis();
                System.out.println("\n" + i + " : " + (endTime - startTime));
            }*/
        }

        System.out.print("\n *****************************************************************************************************\n");


        //Convert the HashMap<HashSet>> data structure of random graph mapping object to List<List> data structure
        //This is done because vartika's code is accepting graph input in the form of List<List> data structure
        AdjacencyMapping randomAdjMap = convert.convertToAdjacencyMapping(randomGraphAdjacenyList);
       // convert.print(randomAdjMap);
        return (new Mapping(randomEdgeList, randomAdjMap));
    }


    //Number of times the swapping needs to be performed for getting random graphs
    public long getQE()
    {
       // System.out.println(Q * inputGraph.getLinkCount());
        return (Q * inputGraph.getLinkCount());
    }

    /**
     * Get the next edge present in random graph mapping object.
     * @param vertex_edge : array of 2 vertex
     * @param adjList : random graph mapping object
     * @return
     */
    int[] getNextEdge(int []vertex_edge, Hashtable<Integer, HashSet<Integer>> adjList)
    {

        int to = vertex_edge[0];
        int from = vertex_edge[1];

        boolean flag = false;

        //Get the next vertex forming an edge in random graph mapping object
        for(Iterator iter = adjList.get(to).iterator();iter.hasNext();)
        {
            int nextVertex = (Integer)iter.next();
            if(nextVertex == from)
            {
                  if(iter.hasNext())
                  {
                      vertex_edge[1] = (Integer) iter.next();
                      flag =true;
                      break;
                  }

            }
            else
            {
                vertex_edge[1] = nextVertex;
                flag = true;
                break;
            }
        }

        // if to from vertex fails to create new edge then reverse the to from vertex and search next vertex

        if(!flag)
        {
            for(Iterator iter = adjList.get(from).iterator(); iter.hasNext();)
            {
                int nextVertex = (Integer)iter.next();
                vertex_edge[0] = from;
                if(nextVertex == to)
                {
                    if (iter.hasNext())
                    {
                        vertex_edge[1] = (Integer) iter.next();
                        flag = true;
                        break;
                    }

                }
                else
                {
                    vertex_edge[1] = nextVertex;
                    flag= true;
                    break;
                }
            }
        }

        //if new edges is not found then return -1.
        if(!flag)
        {
            vertex_edge[0] = -1;
            vertex_edge[1] = -1;
        }
        return vertex_edge;
    }

    /**
     *Check if all the four vertexes are unique or not. So that while swapping we do not create self loop
     * @param vertex_edge1 : pair of vertexes for edge 1
     * @param vertex_edge2 : pair of vertexes for edge 2
     * @return
     */
    public boolean isEdgeUnique(int []vertex_edge1, int[]vertex_edge2)
    {
        //if all the four vertexes are unique return true else return false
        if(vertex_edge1[0] == vertex_edge2[1] || vertex_edge1[0] == vertex_edge2[0])
        {
            return false;
        }
        if(vertex_edge1[1] == vertex_edge2[1] || vertex_edge1[1] == vertex_edge2[0])
        {
            return false;
        }

        return true;
    }

    /**
     * Check the existence of edge within an random graph mapping object
     * @param vertex : pair of vertexes for edge
     * @param adjList : random graph mapping object
     * @return
     */
    public boolean checkEdgeExistence(int[]vertex,Hashtable<Integer, HashSet<Integer>> adjList)
    {
        //if edge exists then return true else return false
        return (adjList.containsKey(vertex[0])?(adjList.get(vertex[0]).contains(vertex[1])? true : false): false);
    }

    /**
     * randomly generate two pair of vertexes
     * @return
     */
    public int[] getToFromVertex()
    {
        int[]vertex = new int[2];
        while(true)
        {
            //randomly generate a vertex
            vertex[0] = randomNumberGenerator(inputGraph.getNodeCount());
            vertex[1] = randomNumberGenerator(inputGraph.getNodeCount());

            if(vertex[0] != vertex[1])
                break;
        }

        return vertex;
    }

    /**
     * random generator
     * @param n : number of vertex in input graph mapping object
     * @return
     */
    public int randomNumberGenerator(int n)
    {
       long seed =  System.currentTimeMillis();
        Random rand = new Random(seed);
        return rand.nextInt(n);
    }

    /**
     * Swap the vertexes in random graph mapping object
     * @param vertex_edge1 : pair of vertexes for edge 1
     * @param vertex_edge2 : pair of vertexes for edge 2
     * @param adjList : rnadom graph mapping object
     * @param i : number of swapping
     * @return
     */
    boolean swap(int[] vertex_edge1, int[] vertex_edge2,Hashtable<Integer, HashSet<Integer>> adjList, long i)
    {
        boolean flag = true;
        if(flag)
        {
            //check if the edge created by swappin the vertex is forming multiple edges or not
            if(checkEdgeExistence(new int[] {vertex_edge1[0],  vertex_edge2[0]}, adjList ))
            {
                flag = false;

            }

            //check if the edge created by swappin the vertex is forming multiple edges or not
            if(checkEdgeExistence(new int[] {vertex_edge1[1],  vertex_edge2[1]}, adjList ))
            {
                flag = false;

            }

            //if multiple edge is not created then swap the vertex and create new edge.
            if(flag)
            {
                addEdges(adjList, vertex_edge1[0], vertex_edge2[0]);
                addEdges(adjList, vertex_edge1[1], vertex_edge2[1]);
            }
        }

        //if above swappign is creating multiple edges then swap different pair of vertexes
        if(!flag)
        {
            //check if the edge created by swappin the vertex is forming multiple edges or not
            if(checkEdgeExistence(new int[] {vertex_edge1[0],  vertex_edge2[1]}, adjList))
            {
                return false;
            }
            //check if the edge created by swappin the vertex is forming multiple edges or not
            if (checkEdgeExistence(new int[] {vertex_edge1[1],  vertex_edge2[0]}, adjList ))
            {
                return false;
            }
            flag = true;

            //if multiple edge is not created then swap the vertex and create new edge.
            if(flag)
            {
                addEdges(adjList, vertex_edge1[0], vertex_edge2[1]);
                addEdges(adjList, vertex_edge1[1], vertex_edge2[0]);
            }
        }
        // Delete the old edges
        if(flag)
        {
            deleteEdges(adjList, vertex_edge1[0], vertex_edge1[1]);
            deleteEdges(adjList, vertex_edge2[0], vertex_edge2[1]);
        }

        return true;
    }

    /**
     * Create an edge between new pair of vertices.
     * @param adjList : random graph mapping object
     * @param to : vertex 1
     * @param from : vertex 2
     */
    public void addEdges(Hashtable<Integer, HashSet<Integer>> adjList, int to, int from)
    {
        HashSet<Integer> set1 = null;
        HashSet<Integer> set2 = null;

        if(adjList.containsKey(to))
        {
           set1 = adjList.get(to);
           set1.add(from);
        }
        else
        {
            set1 = new HashSet<Integer>();
            set1.add(from);

        }
        if(adjList.containsKey(from))
        {
           set2 = adjList.get(from);
           set2.add(to);
        }
        else
        {
            set2 = new HashSet<Integer>();
            set2.add(to);
        }

        //Since this is an undirected graph then add an edge from to->from and from->to
        adjList.put(to, set1);
        adjList.put(from, set2);

    }

    /**
     * Delete old edges from random graph mapping object
     * @param adjList : random graph mapping object
     * @param to : vertex 1
     * @param from : vertex2
     */
    public void deleteEdges(Hashtable<Integer, HashSet<Integer>> adjList, int to, int from)
    {
        if(adjList.containsKey(to))
        {
            adjList.get(to).remove(from);
        }

        if(adjList.containsKey(from))
        {
            adjList.get(from).remove(to);
        }
    }
}


