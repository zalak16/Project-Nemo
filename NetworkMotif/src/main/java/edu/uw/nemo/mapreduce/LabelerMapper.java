/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * Maps the g6 format string as the key so that all graphs with a particular g6
 * value are passed to the reducer which computes the canonical label
 *
 * @author vartikav
 */
public class LabelerMapper extends Mapper<LongWritable, Text, Text, IntWritable> {

    private final static IntWritable one = new IntWritable(1);

    /**
     * Reads the input file with sub-graphs and their g6 representation and pass
     * the g6 representation as the key for reduce phase
     *
     * @param key
     * @param value Tab separated sub-graph and its g6 representation
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void map(LongWritable key, Text value, Context context)
            throws IOException, InterruptedException {

        // read the input
        String input = value.toString().trim();
        String[] split = input.split("[ \\t]");
        boolean onlyG6Present = context.getConfiguration().getBoolean(LabelerJob.OnlyG6Present, false);
        String g6Label = onlyG6Present ? split[0].trim() : split[1].trim();

        // collect the output
        context.write(new Text(g6Label), one);
    }
}
