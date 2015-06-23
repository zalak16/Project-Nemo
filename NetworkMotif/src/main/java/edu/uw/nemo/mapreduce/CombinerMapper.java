/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * A pass through mapper which just splits the input on tab and set the
 * canonical label as key and the other part as value
 *
 * @author vartikav
 */
public class CombinerMapper extends Mapper<LongWritable, Text, Text, Text> {

    /**
     * Split the input and sets the canonical label as key, so that it is
     * collected and aggregated by reducer later
     *
     * @param key
     * @param value A tab separated line with canonical label and the actual
     * graph in g6 representation with count
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        String input = value.toString().trim();
        String[] split = input.split("[ \\t]");
        String canonicalLabel = split[0].trim();
        String g6LabelAndCount = split[1].trim();

        context.write(new Text(canonicalLabel), new Text(g6LabelAndCount));
    }
}
