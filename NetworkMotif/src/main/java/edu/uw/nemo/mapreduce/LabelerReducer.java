/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;

import edu.uw.nemo.labeler.GraphLabel;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Computes the canonical label for the sub-graphs using labelg
 *
 * @author vartikav
 */
public class LabelerReducer extends Reducer<Text, IntWritable, Text, Text> {

    /**
     * Counts the total number of sub-graphs with a given g6 representation and
     * computes the canonical label for the g6 representation.
     *
     * @param key g6 representation of the sub-graph
     * @param values List of 1s for the number of sub-graphs with {@code key} as
     * g6 representation
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<IntWritable> values, Context context)
            throws IOException, InterruptedException {

        // compute canonical label
        String g6String = key.toString().trim();
        String labelgPath = "./" + LabelerJob.LabelGFile;
        String canonicalLabel = GraphLabel.getCanonicalLabel(labelgPath, g6String);

        // compute count of sub-graphs
        long sum = 0;
        for (IntWritable one : values) {
            sum += one.get();
        }

        // write output
        context.write(new Text(canonicalLabel), new Text(g6String + "#" + sum));
    }
}
