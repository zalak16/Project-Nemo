/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import edu.uw.nemo.io.Parser;
import edu.uw.nemo.labeler.FormatType;
import edu.uw.nemo.labeler.GraphFormat;
import edu.uw.nemo.model.AdjacentVertexWithEdge;
import edu.uw.nemo.model.Mapping;
import edu.uw.nemo.model.SubGraph;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Implements the extension part of the ESU algorithm with G6 format computation
 *
 * @author vartikav
 */
public class EsuMapper extends Mapper<LongWritable, Text, Text, Text> {

    private static Mapping mapping = null;
    private static boolean skipG6;
    private static boolean generateG6;

    /**
     * One time setup to initialize the mapping object.
     * 
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Context context)
            throws IOException, InterruptedException {
        if (mapping == null) {
            String mapperFilePath = "./" + ESUJob.MapperFileName;
            mapping = parseFile(mapperFilePath);
        }

        skipG6 = context.getConfiguration().getBoolean(ESUJob.SkipG6ParameterKey, false);
        generateG6 = context.getConfiguration().getBoolean(ESUJob.GenerateG6InMapper, true);
        
        super.setup(context);
    }

    /**
     * Extends the subgraph in the input file by one vertex
     *
     * @param key
     * @param value
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // read input       
        String input = value.toString().trim();
        String[] split = input.split("[ \\t]");
        String inputGraph = split[0].trim();

        String[] graph = inputGraph.split(",");
        int[] subgraphNodes = new int[graph.length];
        int i = 0;
        int subgraphLength = subgraphNodes.length;
        SubGraph subGraph = new SubGraph(subgraphLength + 1, true);
        for (String node : graph) {
            subgraphNodes[i] = Integer.parseInt(node);
            subGraph.add(subgraphNodes[i]);
            i++;
        }

        // find the extension for the subgraph
        Set<Integer> extension = getSubGraphNeighbourhood(subgraphNodes);
        for (Integer v : extension) {
            // add the vertex from extension into the subgraph
            subGraph.add(v);
            // compute the g6 for the sub-graph
            // if skipG6 is not true and generateG6 is true
            String g6String = "";
            if (generateG6 && !skipG6) {
                GraphFormat g6 = new GraphFormat(FormatType.Graph6, mapping, subGraph.getSortedVertices());
                g6String = g6.toString();
            }
            
            // collect the output
            context.write(new Text(subGraph.toString()), new Text(g6String));
            // now remove the last added vertex
            subGraph.remove();
        }
    }

    private Set<Integer> getSubGraphNeighbourhood(int[] subGraph) {
        Set<Integer> neighbourhood = new HashSet<Integer>();
        int root = subGraph[0];
        for (int i = 0; i < subGraph.length; i++) {
            for (AdjacentVertexWithEdge v : mapping.getNeighbours(subGraph[i])) {
                if (v.getNodeId() <= root) {
                    continue;
                }
                
                boolean isInExclusiveNeighbourhood = true;
                for (int n : subGraph) {
                    if (v.getNodeId() == n) {
                        isInExclusiveNeighbourhood = false;
                        break;
                    }
                }

                if (isInExclusiveNeighbourhood) {
                    neighbourhood.add(v.getNodeId());
                }
            }
        }

        return neighbourhood;
    }

    private static Mapping parseFile(String fileName)
    {
        Parser parser = new Parser();
        Mapping _mapping = null;
        try
        {
            _mapping = parser.parser(fileName);
        }
        catch (IOException ioe)
        {
            System.err.println("Exception while parsing file for mapping. " + ioe.getLocalizedMessage());
        }
        catch (URISyntaxException urie)
        {
            System.err.println("Exception while parsing file for mapping. " + urie.getLocalizedMessage());
        }

        return _mapping;
    }
}
