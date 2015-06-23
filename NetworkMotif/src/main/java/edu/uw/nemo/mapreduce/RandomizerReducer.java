/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * The reducer gets the randomized input from map tasks based on the random key
 * used by map tasks and just outputs the values after splitting them on TAB
 * @author vartikav
 */
public class RandomizerReducer extends Reducer<Text, Text, Text, Text> {
    
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        for (Text value : values) {
            String[] split = value.toString().split("[ \\t]");
            context.write(new Text(split[0]), new Text(split[1]));
        }
    }
}
