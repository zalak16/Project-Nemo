/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;
import java.io.PrintStream;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Runs a map-reduce job to collect the canonical labels to produce their total
 * count
 *
 * @author vartikav
 */
public class CombinerJob {

    private Configuration conf;

    public CombinerJob() {
        conf = new Configuration();
    }

    public void run(String args[])
            throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException {
        FileSystem fs = FileSystem.get(this.conf);
        
        // Create file to log information
        Path logFile = new Path(args[2], "Combiner.log");
        PrintStream logFileStream = new PrintStream(fs.create(logFile, true), true, "UTF-8");
        long start = System.currentTimeMillis();
        
        Job job = createJob(args);
        job.setNumReduceTasks(1);
        FileInputFormat.setInputPaths(job, new Path(args[0], "CanonicalLabels"));
        FileOutputFormat.setOutputPath(job, new Path(args[1], "Motifs"));
        job.waitForCompletion(true);
        
        long end = System.currentTimeMillis();
        logFileStream.println("Combining labels to get frequency took " + (end - start) + " ms.");
        logFileStream.close();
    }

    private Job createJob(String[] args)
            throws IOException, URISyntaxException {
        Job job = Job.getInstance(this.conf, "combinerjob");
        job.setJar(args[3]);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(CombinerMapper.class);
        job.setReducerClass(CombinerReducer.class);

        return job;
    }
}
