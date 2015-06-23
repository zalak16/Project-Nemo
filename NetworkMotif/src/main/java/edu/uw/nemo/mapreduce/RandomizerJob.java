/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Runs a map-reduce job to randomize the input
 *
 * @author vartikav
 */
public class RandomizerJob {
    private Configuration conf;
    private int numberOfReducersOverride;

    /**
     * Constructs the randomizer job
     * @param numberOfReducersOverride The number of reducers to use for this job.
     */
    public RandomizerJob(int numberOfReducersOverride) {
        conf = new Configuration();
        this.numberOfReducersOverride = numberOfReducersOverride;
    }

    /**
     * Runs the randomizer job
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException 
     */
    public void run(String args[])
            throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException {
        Job job = createJob(args);
        if (this.numberOfReducersOverride > 0) {
            job.setNumReduceTasks(numberOfReducersOverride);
        }
        
        FileInputFormat.setInputPaths(job, new Path(args[0]));
        FileOutputFormat.setOutputPath(job, new Path(args[1]));
        job.waitForCompletion(true);
    }

    private Job createJob(String[] args)
            throws IOException, URISyntaxException {
        Job job = Job.getInstance(this.conf, "randomizerjob");
        job.setJar(args[2]);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(RandomizerMapper.class);
        job.setReducerClass(RandomizerReducer.class);

        return job;
    }
}
