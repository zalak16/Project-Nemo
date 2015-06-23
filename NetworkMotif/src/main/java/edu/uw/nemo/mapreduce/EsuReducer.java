/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import edu.uw.nemo.io.Parser;
import edu.uw.nemo.labeler.FormatType;
import edu.uw.nemo.labeler.GraphFormat;
import edu.uw.nemo.model.Mapping;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * A de-duping reducer
 *
 * @author vartikav
 */
public class EsuReducer extends Reducer<Text, Text, Text, Text> {

    private static Mapping mapping = null;
    private static boolean skipG6;
    private static boolean generateG6;
    private static boolean skipEnumeratingSubgraphs;

    /**
     * One time setup to initialize the mapping object
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
        generateG6 = !(context.getConfiguration().getBoolean(ESUJob.GenerateG6InMapper, true));
        skipEnumeratingSubgraphs = context.getConfiguration().getBoolean(ESUJob.SkipEnumeratingSubgraphs, false);
        
        super.setup(context);
    }
    
    /**
     * It gets the enumerated sub-graphs from map stage.
     * There could be multiple sub-graphs with same vertices.
     * Since the sorted list of vertices is the key of map stage,
     * the reducer gets all the duplicate sub-graphs together and just produce single output.
     * @param key The sorted list of vertices of sub-graph
     * @param values The g6 representations (all should be same)
     * @param context
     * @throws IOException
     * @throws InterruptedException 
     */
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        Text subGraph = null;
        for (Text value : values) {
            // just read the first value
            
            // compute the g6 for the sub-graph
            // if skipG6 is not true and generateG6 is true
            Text g6Text = value;
            if (generateG6 && !skipG6) {
                String[] subGraphVerticesAsStringList = key.toString().split(",");
                int[] subGraphVertices = new int[subGraphVerticesAsStringList.length];
                for (int i = 0; i < subGraphVertices.length; i++) {
                    subGraphVertices[i] = Integer.parseInt(subGraphVerticesAsStringList[i]);
                }
                
                GraphFormat g6 = new GraphFormat(FormatType.Graph6, mapping, subGraphVertices);
                g6Text = new Text(g6.toString());
            }
            
            subGraph = g6Text;
            break;
        }
        
        if (subGraph != null) {
            if (skipEnumeratingSubgraphs) {
                context.write(subGraph, new Text(""));
            }
            else {
                context.write(key, subGraph);
            }
        }
    }
    
    private static Mapping parseFile(String fileName) {
        Parser parser = new Parser();
        Mapping _mapping = null;
        try {
            _mapping = parser.parser(fileName);
        } catch (IOException ioe) {
            System.err.println("Exception while parsing file for mapping. " + ioe.getLocalizedMessage());
        } catch (URISyntaxException urie) {
            System.err.println("Exception while parsing file for mapping. " + urie.getLocalizedMessage());
        }

        return _mapping;
    }
}
