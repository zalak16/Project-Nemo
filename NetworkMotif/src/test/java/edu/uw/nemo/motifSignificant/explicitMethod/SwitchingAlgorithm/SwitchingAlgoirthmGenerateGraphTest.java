package edu.uw.nemo.motifSignificant.explicitMethod.SwitchingAlgorithm;

import edu.uw.nemo.io.Parser;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.motifSignificant.explicitMethod.preserveNumberOfVertexAndEdges.GenerateGraph;
import org.junit.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Hashtable;

import static org.junit.Assert.*;

/**
 * Created by Zalak on 5/6/2015.
 */
public class SwitchingAlgoirthmGenerateGraphTest
{
    private static final String FileName = "InputGraph2.csv";
    int vertex_Edge1[];
    int vertex_Edge2[];
    SwitchingAlgoirthmGenerateGraph generateGraph = null;
    Hashtable<Integer, HashSet<Integer>> adjList = null;
    Mapping inputGraph = null;

    @Before
    public void setUp() throws IOException, URISyntaxException
    {
        Parser parser = new Parser();
        inputGraph = parser.parser(FileName);
        generateGraph = new SwitchingAlgoirthmGenerateGraph(inputGraph);

        vertex_Edge1 = new int[2];
        vertex_Edge2 = new int[2];
        vertex_Edge1[0] = 1;
        vertex_Edge1[1] = 2;

        vertex_Edge2[0] = 2;
        vertex_Edge2[1] = 4;

        adjList = new Hashtable<Integer, HashSet<Integer>>();

        HashSet<Integer> set = new HashSet<Integer>();
        set.add(1);
        set.add(2);
        adjList.put(0, set) ;

        set = new HashSet<Integer>();
        set.add(0);
        set.add(3);
        adjList.put(1, set);

        set = new HashSet<Integer>();
        set.add(0);
        adjList.put(2, set);

        set = new HashSet<Integer>();
        set.add(1);
        adjList.put(3, set);


    }

    @After
    public void tearDown() {

        adjList.clear();
        generateGraph = null;
        vertex_Edge2 = null;
        vertex_Edge1 = null;
        inputGraph = null;
    }

    @Test
    public void testNodecountAndEdgeCount() throws Exception
    {

        Mapping randomGraph = generateGraph.generateGraph();
        assertEquals(true, ((inputGraph.getNodeCount() == randomGraph.getNodeCount()) && (inputGraph.getTotalEdges() == randomGraph.getTotalEdges())));
    }

    @Test
    public void testPreserveDegreeSequence()
    {
        Mapping randomGraph = generateGraph.generateGraph();
        for(int i= 0; i< randomGraph.getNodeCount(); i++)
        {
            assertTrue(inputGraph.getNeighbours(i).size() == randomGraph.getNeighbours(i).size());
        }
    }



    @Test
    public void testGraphDifference() throws IOException, URISyntaxException {

        Mapping randomGraph = generateGraph.generateGraph();
        int differenceCount = 0;
        for(int i= 0; i<inputGraph.getNodeCount(); i++)
        {
            for(AdjacentVertexWithEdge adjacentVertex: inputGraph.getNeighbours(i))
            {
                if(randomGraph.getNeighbour(i, adjacentVertex.getNodeId()) == null)
                {
                    differenceCount++;
                }

            }
        }

        assertTrue(differenceCount > 0);
        assertFalse(differenceCount == 0);
    }

    @Test
    public void testGetQE() throws Exception
    {
        assertEquals(6, generateGraph.getQE());

        generateGraph = null;
     }

    @Test
    public void testGetNextEdge() throws Exception
    {
        vertex_Edge1[0] = 1;
        vertex_Edge1[1] = 2;

        vertex_Edge1 = generateGraph.getNextEdge(vertex_Edge1, adjList);

        assertEquals(0, vertex_Edge1[1]);

        vertex_Edge1[0] = 2;
        vertex_Edge1[1] = 0;

        vertex_Edge1 = generateGraph.getNextEdge(vertex_Edge1, adjList);

        assertEquals(1, vertex_Edge1[1]);
    }

    @Test
    public void testIsEdgeUnique() throws Exception
    {
       assertFalse(generateGraph.isEdgeUnique(vertex_Edge1, vertex_Edge2));

        vertex_Edge1[0] = 2;
        vertex_Edge1[1] = 3;

        vertex_Edge2[0] = 1;
        vertex_Edge2[1] = 4;

        assertTrue(generateGraph.isEdgeUnique(vertex_Edge1, vertex_Edge2));

    }

    @Test
    public void testCheckEdgeExistence() throws Exception
    {
        int[] vertex = new int[2];
        vertex[0] = 0;
        vertex[1] = 2;

        assertTrue(generateGraph.checkEdgeExistence(vertex,adjList));

        vertex[0] = -1;
        vertex[1] = 2;
        assertFalse(generateGraph.checkEdgeExistence(vertex, adjList));
    }

    @Test
    public void testGetToFromVertex() throws Exception
    {
       Parser parser = new Parser();
       Mapping inputGraph = parser.parser(FileName);
        generateGraph = new SwitchingAlgoirthmGenerateGraph(inputGraph);
        int [] vertex = generateGraph.getToFromVertex();
        assertTrue(vertex.length == 2);
        assertTrue(vertex[0] >= 0 && vertex[0] < inputGraph.getNodeCount());
        assertTrue(vertex[1] >= 0 && vertex[1] < inputGraph.getNodeCount());
        assertTrue(vertex[0] != vertex[1]);

        parser = null;
        inputGraph = null;


    }

    @Test
    public void testRandomNumberGenerator() throws Exception {

        assertNotNull(inputGraph);
        int n = generateGraph.randomNumberGenerator(inputGraph.getNodeCount());
        assertTrue(n >= 0 && n < inputGraph.getNodeCount());
        assertFalse(n >= inputGraph.getNodeCount());


        inputGraph = null;
    }

    @Test
    public void testSwap() throws Exception
    {

        vertex_Edge1[0] = 0;
        vertex_Edge1[1] = 2;

        vertex_Edge2[0] = 1;
        vertex_Edge2[1] = 3;

        assertEquals(true,generateGraph.swap(vertex_Edge1, vertex_Edge2, adjList,0));

        assertEquals(false,generateGraph.swap(vertex_Edge1, vertex_Edge2, adjList, 0) );

        vertex_Edge1[0] = 1;
        vertex_Edge1[1] = 2;

        vertex_Edge2[0] = 0;
        vertex_Edge2[1] = 3;

        assertTrue(generateGraph.swap(vertex_Edge1, vertex_Edge2, adjList, 1));
    }

    @Test
    public void testAddEdges() throws Exception
    {
         int to = 0;
        int from = 3;

        assertFalse(adjList.get(to).contains(from));
        assertFalse(adjList.get(from).contains(to));

        generateGraph.addEdges(adjList, to, from);

        assertTrue(adjList.get(to).contains(from));
        assertTrue(adjList.get(from).contains(to));

    }

    @Test
    public void testDeleteEdges() throws Exception
    {
        int to = 0;
        int from = 2;

        assertTrue(adjList.get(to).contains(from));
        assertTrue(adjList.get(from).contains(to));

        generateGraph.deleteEdges(adjList, to, from);

        assertFalse(adjList.get(to).contains(from));
        assertFalse(adjList.get(from).contains(to));

    }
}