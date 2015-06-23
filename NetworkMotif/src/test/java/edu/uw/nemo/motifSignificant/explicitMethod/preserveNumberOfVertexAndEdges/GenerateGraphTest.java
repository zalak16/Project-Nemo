package edu.uw.nemo.motifSignificant.explicitMethod.preserveNumberOfVertexAndEdges;

import edu.uw.nemo.io.Parser;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.*;

/**
 * Created by Zalak on 5/6/2015.
 */
public class GenerateGraphTest {

    private static final String FileName= "InputGraph2.csv";

    @Test
    public void testPreserveNodecountAndEdgeCount() throws Exception
    {
        Parser parser = new Parser();
        Mapping inputMapping = parser.parser(FileName);
        GenerateGraph generateGraph = new GenerateGraph(inputMapping);
        Mapping randomMapping = generateGraph.generateRandomGraph();
        assertEquals(true, ((inputMapping.getNodeCount() == randomMapping.getNodeCount()) && (inputMapping.getTotalEdges() == randomMapping.getTotalEdges())));
    }

    @Test
    public void testGraphDifference() throws IOException, URISyntaxException {
        Parser parser = new Parser();
        Mapping inputMapping = parser.parser(FileName);
        GenerateGraph generateGraph = new GenerateGraph(inputMapping);
        Mapping randomMapping = generateGraph.generateRandomGraph();
        int differenceCount = 0;
        for(int i= 0; i<inputMapping.getNodeCount(); i++)
        {
            for(AdjacentVertexWithEdge adjacentVertex: inputMapping.getNeighbours(i))
            {
                if(randomMapping.getNeighbour(i, adjacentVertex.getNodeId()) == null)
                {
                    differenceCount++;
                }

            }
        }

        assertTrue(differenceCount > 0);
        assertFalse(differenceCount == 0);
    }

    @Test
    public void testGetRandomVertex() throws Exception
    {
        Parser parser = new Parser();
        Mapping inputMapping = parser.parser(FileName);
        GenerateGraph generateGraph = new GenerateGraph(inputMapping);
        assertNotNull(inputMapping);
        int n = generateGraph.getRandomVertex(inputMapping.getNodeCount());
        assertTrue(n >= 0 && n < inputMapping.getNodeCount());
        assertFalse(n >= inputMapping.getNodeCount());
    }

    @Test
    public void testCheckEdgeExistence() throws Exception
    {
        Parser parser = new Parser();
        Mapping inputMapping = parser.parser(FileName);
        GenerateGraph generateGraph = new GenerateGraph(inputMapping);
        assertNotNull(inputMapping);
        assertTrue(generateGraph.checkEdgeExistence(inputMapping.getAdjMapping(), 0, 1));
        assertFalse(generateGraph.checkEdgeExistence(inputMapping.getAdjMapping(), 0, 5));
    }

}