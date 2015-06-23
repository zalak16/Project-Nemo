/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Runs a map-reduce job to compute canonical labels for the enumerated
 * sub-graphs
 *
 * @author vartikav
 */
public class LabelerJob {

    public static final String LabelGFile = "labelg";
    public static final String OnlyG6Present = "nemo.Labeler.onlyG6Present";
    private Configuration conf;
    private boolean onlyG6Present;
    private int labelerReducerCount;

    /**
     * Constructs the labeler job
     * 
     * @param onlyG6Present If true, then last ESU iteration only produced the g6 representation, but not actual sub-graphs
     * @param labelerReducerCount Number of reducer task to use for the job
     */
    public LabelerJob(boolean onlyG6Present, int labelerReducerCount) {
        this.conf = new Configuration();
        this.onlyG6Present = onlyG6Present;
        this.labelerReducerCount = labelerReducerCount;
    }

    /**
     * Runs the canonical labeling job
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException 
     */
    public void run(String args[])
            throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException {
        FileSystem fs = FileSystem.get(this.conf);
        
        // Create file to log information
        Path logFile = new Path(args[2], "Labeler.log");
        PrintStream logFileStream = new PrintStream(fs.create(logFile, true), true, "UTF-8");
        long start = System.currentTimeMillis();
        
        Job job = createJob(args);
        FileInputFormat.setInputPaths(job, new Path(args[1]));
        FileOutputFormat.setOutputPath(job, new Path(args[2], "CanonicalLabels"));
        job.waitForCompletion(true);
        
        long end = System.currentTimeMillis();
        logFileStream.println("Generating canonical labels took " + (end - start) + " ms.");
        logFileStream.close();
    }

    private Job createJob(String[] args)
            throws IOException, URISyntaxException {
        if (this.onlyG6Present) {
            this.conf.setBoolean(OnlyG6Present, this.onlyG6Present);
        }
        
        Job job = Job.getInstance(this.conf, "labelerjob");
        job.setJar(args[3]);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(IntWritable.class);

        job.setMapperClass(LabelerMapper.class);
        job.setReducerClass(LabelerReducer.class);

        job.addCacheFile(new URI(args[0] + "#" + LabelGFile));
        
        if (this.labelerReducerCount > 0) {
            job.setNumReduceTasks(this.labelerReducerCount);
        }

        return job;
    }
}
