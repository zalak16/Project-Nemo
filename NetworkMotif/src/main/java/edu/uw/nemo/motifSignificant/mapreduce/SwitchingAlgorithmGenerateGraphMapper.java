package edu.uw.nemo.motifSignificant.mapreduce;

import edu.uw.nemo.model.AdjacencyMapping;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.motifSignificant.explicitMethod.SwitchingAlgorithm.SwitchingAlgoirthmGenerateGraph;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapreduce.Mapper;




import java.io.IOException;
import java.net.InetAddress;
import java.util.List;


/**
 * Created by Zalak on 5/4/2015.
 */
public class SwitchingAlgorithmGenerateGraphMapper extends Mapper<LongWritable, Text, Text, BooleanTwoDArrayWritable>
{
   static Mapping inputMapping = null;
   static int networkSize = GraphGeneratorJob.networkSize;
    BooleanWritable one = new BooleanWritable(true);
    BooleanWritable zero = new BooleanWritable(false);


    BooleanWritable[][] adjMatrix = new BooleanWritable[this.networkSize][this.networkSize];

    Boolean arrayCreated = false;

    protected void setup(Context context) throws IOException, InterruptedException {

        if(GraphGeneratorJob.mapping == null)
        {
            System.out.println("Input file is invalid");
            return;
        }
        else
        {
            inputMapping = GraphGeneratorJob.mapping;
            System.out.println("Status : " + context.getStatus());
            System.out.println("Input Split: " + context.getInputSplit());
            System.out.println("Job Id: " + context.getJobID());
            System.out.println("Job Name: " + context.getJobName());


        }
        super.setup(context);

    }
    public void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException
    {
        InetAddress iAddress = InetAddress.getLocalHost();
        String currentIP = iAddress.getHostAddress();
        System.out.println("Current IP AddressL:  " + currentIP );


        String outputKey = "RandomGraph#" + value.toString();
        SwitchingAlgoirthmGenerateGraph graphGenerator = new SwitchingAlgoirthmGenerateGraph(this.inputMapping);
        Mapping randomGraph = graphGenerator.generateGraph();

        System.out.println("Mapping " + outputKey);
      //  print(randomGraph.getAdjMapping());

        System.out.println("Calling Context.write");
        BooleanWritable[][] adjMatrix1 = convertAdjMappingToMatrix(randomGraph);



        BooleanTwoDArrayWritable twoDArray = new BooleanTwoDArrayWritable();
        if(twoDArray == null)
        {
            System.out.println("Two d Array is null ");
        }
        twoDArray.set(adjMatrix1);


        context.write(new Text(outputKey), twoDArray);
     //   System.out.println("Called Context.write");
    }


    BooleanWritable[][] convertAdjMappingToMatrix(Mapping map)
    {
        AdjacencyMapping adjMap = map.getAdjMapping();

        for(int i =0; i < map.getNodeCount(); i++)
        {
            for(int j = 0; j < map.getNodeCount(); j++)
            {
                this.adjMatrix[i][j] = this.zero;
            }

            List<AdjacentVertexWithEdge> vertexList = adjMap.getNeighbours(i);
            for(AdjacentVertexWithEdge  v : vertexList)
            {
             //   System.out.println(i + " : " + v.getNodeId());
                this.adjMatrix[i][v.getNodeId()] = this.one;
            }
        }
        return this.adjMatrix;
    }


    /**
     *
     * @param map
     */
    private void print(AdjacencyMapping map) {
        System.out.println("\n" + "----------------------------------Mapper----------------------------------------\n");
        for (int i = 0; i < map.size(); i++) {
            List<AdjacentVertexWithEdge> adjList = map.getNeighbours(i);
            System.out.print("\n" + i);
            for (AdjacentVertexWithEdge v : adjList) {
                System.out.print("->" + v.getNodeId());
            }
        }
    }
}
