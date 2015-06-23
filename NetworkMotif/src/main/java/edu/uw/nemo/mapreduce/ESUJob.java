/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.InputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.Random;

import edu.uw.nemo.io.Parser;
import edu.uw.nemo.model.Mapping;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

/**
 * Iterative map-reduce job to enumerate sub-graphs using ESU algorithm
 *
 * @author vartikav
 */
public class ESUJob {

    public static final String MapperFileName = "mapper";
    public static final String SkipG6ParameterKey = "nemo.ESU.skipG6";
    public static final String GenerateG6InMapper = "nemo.ESU.generateG6InMapper";
    public static final String SkipEnumeratingSubgraphs = "nemo.ESU.skipEnumeratingSubgraphs";
    private Configuration conf;
    private boolean randomize;
    private int initialReducerCount;
    private int growthExponent;
    private int timeout;
    private long maxSplitSize;
    private boolean skipG6;
    private boolean generateG6InReducer;
    private boolean skipEnumeratingSubgraphs;
    private int esuIterationStart;

    /**
     * Constructor for ESU jobs
     * @param randomize If true, will randomize output of each iteration of ESU job before running the next iteration.
     * @param initialReducerCount Number of reducers in second iteration of ESU. First iteration uses only one reducer.
     * @param growthExponent The exponent factor in growth of number of reducers with ESU iterations.
     * @param timeout Timeout value (in ms) for map and reduce tasks.
     * @param maxSplitSize The maximum split size (in bytes) of input file that a map task will process.
     * @param skipG6 If true, will skip generating g6 representation in all but last ESU iteration
     * @param generateG6InReducer If true, will not compute g6 representation in map task, but will delegate to reduce task
     * @param skipEnumeratingSubgraphs If true, will not save the enumerated sub-graphs in the last iteration.
     * @param esuIterationStart Will start ESU from the specified iteration count.
     */
    public ESUJob(
            boolean randomize,
            int initialReducerCount,
            int growthExponent,
            int timeout,
            long maxSplitSize,
            boolean skipG6,
            boolean generateG6InReducer,
            boolean skipEnumeratingSubgraphs,
            int esuIterationStart) {
        conf = new Configuration();
        this.randomize = randomize;
        this.initialReducerCount = initialReducerCount;
        this.growthExponent = growthExponent;
        this.timeout = timeout;
        this.maxSplitSize = maxSplitSize;
        this.skipG6 = skipG6;
        this.generateG6InReducer = generateG6InReducer;
        this.skipEnumeratingSubgraphs = skipEnumeratingSubgraphs;
        this.esuIterationStart = esuIterationStart;
    }

    /**
     * Runs the ESU map-reduce jobs iteratively
     * @param args
     * @throws ClassNotFoundException
     * @throws IOException
     * @throws InterruptedException
     * @throws URISyntaxException 
     */
    public void run(String args[])
            throws ClassNotFoundException, IOException, InterruptedException, URISyntaxException
    {
        FileSystem fs = FileSystem.get(this.conf);
        
        // Create file to log information
        Path logFile = new Path(args[1], "ESU.log");
        PrintStream logFileStream;
        if (!fs.exists(logFile)) {
            logFileStream = new PrintStream(fs.create(logFile, true), true, "UTF-8");
        }
        else
        {
            BufferedReader olderLogStream = new BufferedReader(new InputStreamReader(fs.open(logFile)));
            LinkedList<String> oldData = new LinkedList<String>();
            String line = olderLogStream.readLine();
            while (line != null) {
                oldData.add(line);
                line = olderLogStream.readLine();
            }
            
            olderLogStream.close();
            
            logFileStream = new PrintStream(fs.create(logFile, true), true, "UTF-8");
            for (String oldLine : oldData) {
                logFileStream.println(oldLine);
            }
        }
        
        long start = System.currentTimeMillis();
        long begin = start;
        
        // Parse biological network file to create mapping
        Path biologicalNetworkFilePath = new Path(args[0]);
        Mapping mapping = parseFile(fs.open(biologicalNetworkFilePath));
        
        long end = System.currentTimeMillis();
        logFileStream.println("Parsing graph file took " + (end - start) + " ms.");
        start = end;
        
        // Make new file here containing the input graphs and set it as input
        Path inputFilePath = new Path(args[1], "initialSubGraphs.txt");
        if (esuIterationStart == 0) {
            FSDataOutputStream fileOutputStream = fs.create(inputFilePath, true);
            PrintStream graphWriter = null;
            try {
                graphWriter = new PrintStream(fileOutputStream, true, "UTF-8");
                int nodeCount = mapping.getNodeCount();
                int[] nodes = new int[nodeCount];
                for (int i = 0; i < nodeCount; i++) {
                    nodes[i] = i;
                }

                Random random = new Random(System.currentTimeMillis());
                for (int i = 0; i < nodeCount; i++) {
                    int randomNode = random.nextInt(nodeCount - i);
                    graphWriter.println(nodes[randomNode]);
                    nodes[randomNode] = nodes[nodeCount - i - 1];
                }
            } catch (IOException ioe) {
                logFileStream.println("Error while writing graphs: " + ioe.getMessage());
            } finally {
                if (graphWriter != null) {
                    graphWriter.close();
                }
            }

            end = System.currentTimeMillis();
            logFileStream.println("Creating initial sub-graphs took " + (end - start) + " ms.");
            start = end;
        }
        
        int size = Integer.parseInt(args[2]);
        int iterationCount = esuIterationStart;
        while (keepGoing(iterationCount, size)) {
            String input;
            if (iterationCount == 0) {
                input = inputFilePath.toUri().getRawPath();
            } else if (this.randomize) {
                input = args[1] + iterationCount + "Random";
            }
            else {
                input = args[1] + iterationCount;
            }

            String output = args[1] + (iterationCount + 1);

            Job job = createJob(args, iterationCount == size - 2);
            int numberOfReducersOverride = -1;
            if (this.initialReducerCount > 0) {
                numberOfReducersOverride = iterationCount < 1 ? 1 : (int)Math.pow(this.growthExponent, iterationCount - 1) * initialReducerCount;
                job.setNumReduceTasks(numberOfReducersOverride);
            }
            
            FileInputFormat.setInputPaths(job, new Path(input));
            FileOutputFormat.setOutputPath(job, new Path(output));
            job.waitForCompletion(true);

            if (this.randomize && iterationCount < size - 2) {
                RandomizerJob randomizerJob = new RandomizerJob(numberOfReducersOverride);
                String[] randomizerJobArgs = new String[3];
                randomizerJobArgs[0] = output;
                randomizerJobArgs[1] = output + "Random";
                randomizerJobArgs[2] = args[3];
                randomizerJob.run(randomizerJobArgs);
            }
            
            iterationCount++;
            end = System.currentTimeMillis();
            logFileStream.println("Iteration " + iterationCount + " took " + (end - start) + " ms.");
            start = end;
        }

        logFileStream.println("Complete ESU job took " + (end - begin) + " ms.");
        logFileStream.close();
    }

    private Job createJob(String[] args, boolean isFinalIteration)
            throws IOException, URISyntaxException {
        this.conf.setInt("mapreduce.job.jvm.numtasks", -1);
        if (this.timeout > 0) {
            this.conf.setInt("mapreduce.task.timeout", this.timeout);
        }
        
        if (this.maxSplitSize > 0) {
            this.conf.setLong("mapreduce.input.fileinputformat.split.maxsize", this.maxSplitSize);
        }
        
        if (!isFinalIteration) {
            this.conf.setBoolean(SkipG6ParameterKey, this.skipG6);
        }
        else {
            this.conf.setBoolean(SkipG6ParameterKey, false);
        }
        
        if (this.generateG6InReducer) {
            this.conf.setBoolean(GenerateG6InMapper, false);
        }
        
        if (isFinalIteration) {
            this.conf.setBoolean(SkipEnumeratingSubgraphs, this.skipEnumeratingSubgraphs);
        }
        
        Job job = Job.getInstance(this.conf, "esujob");
        job.setJar(args[3]);

        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.setMapperClass(EsuMapper.class);
        job.setReducerClass(EsuReducer.class);

        job.addCacheFile(new URI(args[0] + "#" + MapperFileName));
        
        return job;
    }

    private boolean keepGoing(int iterationCount, int size) {
        // totalIterations = Size of subgraph - 1
        // ex: totalIterations = 3 - 1 = 2 for size 3
        int totalIterations = size - 1;
        if (iterationCount >= totalIterations) {
            return false;
        }

        return true;
    }
    
    private static Mapping parseFile(InputStream stream) {
        Parser parser = new Parser();
        Mapping mapping = null;
        try {
            mapping = parser.parser(stream);
        } catch (IOException ioe) {
            System.err.println("Exception while parsing file for mapping. " + ioe.getLocalizedMessage());
        } catch (URISyntaxException urie) {
            System.err.println("Exception while parsing file for mapping. " + urie.getLocalizedMessage());
        }

        return mapping;
    }
}
