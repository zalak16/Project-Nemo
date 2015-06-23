/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;
import java.util.Random;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

/**
 * A randomizing mapper which randomizes the input by using a random key
 *
 * @author vartikav
 */
public class RandomizerMapper extends Mapper<LongWritable, Text, Text, Text> {

    private Random randGen;
    
    /**
     * One time setup to initialize the random generator
     *
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void setup(Context context)
            throws IOException, InterruptedException {
        this.randGen = new Random(System.currentTimeMillis());
        super.setup(context);
    }
    
    @Override
    protected void map(LongWritable key, Text value, Mapper.Context context)
            throws IOException, InterruptedException {

        context.write(new Text(Long.toString(this.randGen.nextLong())), value);
    }
}