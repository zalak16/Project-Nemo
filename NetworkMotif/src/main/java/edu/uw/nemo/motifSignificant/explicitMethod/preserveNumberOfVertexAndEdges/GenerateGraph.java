package edu.uw.nemo.motifSignificant.explicitMethod.preserveNumberOfVertexAndEdges;

import edu.uw.nemo.labeler.GraphLabel;
import edu.uw.nemo.model.AdjacencyMapping;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Zalak on 4/2/2015.
 */
public class GenerateGraph
{
    Mapping inputMapping;

    public GenerateGraph(Mapping inputMapping)
    {
        this.inputMapping = inputMapping;
    }

    public Mapping generateRandomGraph()
    {
        AdjacencyMapping randomAdjMapping = new AdjacencyMapping(inputMapping.getNodeCount());
        List<String[]> randomEdgeList = new ArrayList<String[]>();
        long countEdges = 0;
        while(countEdges < inputMapping.getLinkCount())
        {
            int toVertex = getRandomVertex(inputMapping.getNodeCount());
            int fromVertex = getRandomVertex(inputMapping.getNodeCount());

            if ((toVertex == fromVertex)||(checkEdgeExistence(randomAdjMapping, toVertex, fromVertex)))
            {
                continue;
            }
            else
            {
                randomAdjMapping.addAdjacentVertices(toVertex, fromVertex);
                countEdges++;
            }
        }
        return new Mapping(randomEdgeList, randomAdjMapping);
    }

    public List<Mapping> generateRandomGraph(int n)
    {
        List<Mapping>randomGraphList = new ArrayList<Mapping>();

       // System.out.println("Input Graph");
      //  randomGraphList.add(inputMapping);
      //  print(randomGraphList);
        //randomGraphList.clear();
        long start= System.currentTimeMillis();
        for(int i =0 ; i<n; i++)
        {

            Mapping map = generateRandomGraph();
            randomGraphList.add(map);
            System.out.println("Graph: " + (i + 1));
            if(i == 0 || i == 100 || i == 500 || i == 400 || i ==800 || i == 1000)
            {
                long end = System.currentTimeMillis();
                System.out.println("Time taken to generate " + (i+1) + " random graphs is: " + (end - start));
            }
        }
        System.out.println("Random Graph");
    //    print(randomGraphList);
        return randomGraphList;
    }


    int getRandomVertex(int n)
    {
       long seed = System.currentTimeMillis();
        Random random = new Random(seed);
        return random.nextInt(n);
    }

    public boolean checkEdgeExistence(AdjacencyMapping adjMapping, int toVertex, int fromVertex)
    {
     //   System.out.println("\n" + toVertex + "," + fromVertex + "," + adjMapping.size());
        if(!(toVertex >= adjMapping.size() || fromVertex >= adjMapping.size()))
        {
            AdjacentVertexWithEdge edge = adjMapping.getNeighbour(toVertex, fromVertex);

            if(edge != null)
            {
                return (edge.getEdge()[0] == toVertex && edge.getEdge()[1] == fromVertex);
            }
        }

        return false;
    }

    void print(List<Mapping> maps)
    {
        for(Mapping map: maps)
        {
            System.out.println("\n-----------------------------------\n");
            for(int i= 0; i< map.getNodeCount(); i++)
            {
                List<AdjacentVertexWithEdge> edges = map.getNeighbours(i);
                System.out.print("\n" + i);
                for(AdjacentVertexWithEdge v: edges)
                {
                    System.out.print("- >" + v.getNodeId());
                }
            }

        }
    }
}
