package edu.uw.nemo.motifSignificant.mapreduce;

import edu.uw.nemo.esu.ESUGen;
import edu.uw.nemo.labeler.GraphFormat;
import edu.uw.nemo.labeler.GraphLabel;
import edu.uw.nemo.model.AdjacencyMapping;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.motifSignificant.explicitMethod.RandomGraphCanonicalLabelling;
import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Reducer;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Created by Zalak on 5/4/2015.
 */
public class SwitchingAlgorithmGenerateGraphReducer extends Reducer<Text, BooleanTwoDArrayWritable, Text, Text>
{
    int k;
    double probability;
    //public static final String LabelGFile = "labelg.exe";
    public static final String LabelGFile = "labelg";
    @Override
    protected void setup(Context context) throws IOException, InterruptedException
    {
        k = GraphGeneratorJob.size;
        probability = GraphGeneratorJob.probability;
        super.setup(context);
    }

    protected void reduce(Text key, Iterable<BooleanTwoDArrayWritable> adjMatrices, Context context) throws IOException, InterruptedException {
       System.out.println("Entering reducer");

        System.out.println(key.toString());
        Mapping randomMapping = null;
        //System.out.print("Printing adj matrices: " + adjMatrices.toString());

        for (BooleanTwoDArrayWritable adjMatrix : adjMatrices)
        {
          //  System.out.println("Entering reduce iterable");
            randomMapping = convertAdjMatrixToMapping(adjMatrix.get());
            break;
        }


      //  System.out.println("Enumerating Subgraphs");
        GraphLabel label = new GraphLabel(false);
        this.enumerateSubGraphs(randomMapping, label, k, probability);
        //System.out.println("Number of subgraphs enumerated: " + label.getSubgraphCount());

        // get canonical labels with GraphLabel
        String labelgPath = "./" + LabelGFile;

        Map<String, List<Map.Entry<String, Long>>> canonicalSubgraphs = label.getCanonicalLabels(labelgPath);
       // System.out.println("Number of canonical labels for all enumerated subgraphs: " + canonicalSubgraphs.size());

        RandomGraphCanonicalLabelling canonicalLabel = new RandomGraphCanonicalLabelling(randomMapping, canonicalSubgraphs);

        StringBuilder finalOutput = new StringBuilder();
        for (Map.Entry<String, List<Map.Entry<String, Long>>> e : canonicalSubgraphs.entrySet())
        {
            long count =0;
            System.out.println("Cannonical Label (g6) \"" + e.getKey() + "\" has following Sub Graphs:");
            Map<String, Long> subGraphCounts = GraphFormat.countDistinctGraphs(e.getValue());
            for (Map.Entry<String, Long> c : subGraphCounts.entrySet())
            {
                System.out.println("\tSubGraph (g6) \"" + c.getKey() + "\" has count: " + c.getValue());
                count += c.getValue();

            }

          if(probability < 1.0 && probability > 0.0)
          {
                count = estimateCount100Percent(count, probability);
          }

            System.out.println(e.getKey() + " : " + count);
            finalOutput.append(e.getKey());
            finalOutput.append(":" + count);
            finalOutput.append("\n");
        }
     //   print(randomMapping.getAdjMapping());

        context.write(new Text(key.toString()), new Text(finalOutput.toString()));

    }

    private long estimateCount100Percent(long count, double probability) {
        return (long)((count * 1.0)/probability);
    }

    Mapping convertAdjMatrixToMapping(Writable[][] adjMatrix)
    {
        System.out.println("Converting mattric to mapping in reducer" + adjMatrix.length);
        AdjacencyMapping adjMap = new AdjacencyMapping(adjMatrix.length);
        BooleanWritable one = new BooleanWritable(true);
        List<String[]> randomEdgeList = new ArrayList<String[]>();

        for(int i=0; i< adjMatrix.length; i++)
        {
          //  System.out.println("\n" + adjMatrix[i].length);
            for(int j =0; j < adjMatrix[i].length; j++)
            {
            //    System.out.println(adjMatrix[i][j] + " " +  i + " : " + j  + " ");
                if(adjMatrix[i][j].equals(one))
                {
                  //  System.out.println(adjMatrix[i][j] + "== " +  i + " : " + j  + " ");
                    if (adjMap.size() == 0)
                    {
                        adjMap.addAdjacentVertices(i, j);
                        continue;
                    }
                    if (adjMap.size()-1 >= i)
                    {
                        if (adjMap.getNeighbour(i, j) == null)
                        {
                             adjMap.addAdjacentVertices(i, j);
                            continue;
                        }
                    }
                    if (adjMap.size()-1 >= j)
                    {
                        if (adjMap.getNeighbour(j, i) == null)
                        {
                            adjMap.addAdjacentVertices(j, i);
                            continue;
                        }
                    }

                    if (!(adjMap.size()-1 >= i) || !(adjMap.size()-1 >= j))
                    {
                        adjMap.addAdjacentVertices(i, j);
                    }
                }

            }
        }

        return (new Mapping(randomEdgeList, adjMap));
    }

    private void print(AdjacencyMapping map) {
//        System.out.println("\n" + "-----------------------------------------Reducer---------------------------------\n");

        for (int i = 0; i < map.size(); i++) {
            List<AdjacentVertexWithEdge> adjList = map.getNeighbours(i);
            System.out.print("\n" + i);
            for (AdjacentVertexWithEdge v : adjList) {
                System.out.print("->" + v.getNodeId());
            }
        }
    }

    private void enumerateSubGraphs(Mapping mapping, GraphLabel label, int size, double probability)
    {
        ESUGen generator = new ESUGen(true);
        generator.enumerateSubgraphs(mapping, label, size, probability);
    }

}
