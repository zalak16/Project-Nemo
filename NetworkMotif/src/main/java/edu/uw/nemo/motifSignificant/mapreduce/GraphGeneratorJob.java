package edu.uw.nemo.motifSignificant.mapreduce;

import edu.uw.nemo.io.Parser;
import edu.uw.nemo.model.Mapping;
import org.apache.hadoop.conf.Configuration;

import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;

import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URI;
import java.net.URISyntaxException;



/**
 * Created by Zalak on 5/5/2015.
 */
public class GraphGeneratorJob
{
    public static Mapping mapping;
    private Configuration conf;
    public static double probability;
    public static int size;
    public static int networkSize;


    /**
     *
     */
    public GraphGeneratorJob()
    {
        this.conf = new Configuration();
    }

    /**
     *
     * @param args
     * @throws IOException
     * @throws URISyntaxException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public void run (String args[]) throws IOException, URISyntaxException, ClassNotFoundException, InterruptedException
    {
        FileSystem fs = FileSystem.get(this.conf);

        this.probability = Double.parseDouble(args[3]);
        this.size = Integer.parseInt(args[6]);

        // All folder and files are getting created at path /user/zalak/
        //Create File to log information
        Path logFile = new Path(args[5], "RandomGraphGenerator.log");
        PrintStream logFileStream = new PrintStream(fs.create(logFile, true), true, "UTF-8");

        //Parse biological network file to create mapping object
        long startTime = System.currentTimeMillis();

        Path biologicalNetworkFilePath = new Path(args[0]);
        this.mapping = parseFile(fs.open(biologicalNetworkFilePath));
        this.networkSize = mapping.getNodeCount();

        long endTime = System.currentTimeMillis();
        logFileStream.println("Parsing graph file took " + (endTime - startTime) + "ms.");

        //Create new file containing nuber of graphs to be created and  set it as input
        startTime = System.currentTimeMillis();

        Path inputFilePath = new Path("RandomGraphGenerator" ,"numberRandomGraphs.txt");
        FSDataOutputStream fileOutputStream = fs.create(inputFilePath, true);
        PrintStream fileWriter = null;

        //Number of random graphs to be generated
        int NRandomGraph = Integer.parseInt(args[2]);
        try
        {
           fileWriter = new PrintStream(fileOutputStream, true, "UTF-8");
            for(int i= 0; i< NRandomGraph; i++)
            {
                 fileWriter.println((i+1));
            }
        }
        catch(IOException ex)
        {
            logFileStream.println("Error while writing file: " + ex.getMessage());
        }
        finally
        {
            if(fileWriter != null)
            {
               fileWriter.close();
            }
        }

        endTime = System.currentTimeMillis();
        logFileStream.println("Creating a input file with total random grpahs to be generated took " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        String input = inputFilePath.toUri().getRawPath();
        String output = args[1];

        Job job = createJob(args);

        System.out.println("Max split size: " + FileInputFormat.getMaxSplitSize(job));
        FileInputFormat.setMaxInputSplitSize(job, 100);
        FileInputFormat.setMinInputSplitSize(job, 100);

        FileInputFormat.setInputPaths(job, new Path(input));

        System.out.println("Max split size: " + FileInputFormat.getMaxSplitSize(job));
        FileOutputFormat.setOutputPath(job, new Path(output));

        //submit job to clusters and wait for it to complete
        job.waitForCompletion(true);

        endTime = System.currentTimeMillis();
        logFileStream.println("Complete " + args[2] + " Random Graph Generator job took " + (endTime - startTime) + "ms");
        logFileStream.close();
    }

    /**
     *
     * @param args
     * @return
     * @throws IOException
     * @throws URISyntaxException
     */
    private Job createJob(String args[]) throws IOException, URISyntaxException {

        this.conf.setLong("mapreduce.input.fileinputformat.split.maxsize", 100); // 200 bytes or 500 bytes
        this.conf.setLong("mapreduce.input.fileinputformat.split.minsize", 100);

        Job job = Job.getInstance(this.conf, "randomGraphGeneratorJob");
        job.setJar(args[4]);

        //Specifies Output key and value type for Map and Reduce class.
        job.setOutputKeyClass(Text.class);

       // job.setMapOutputValueClass(MappingObject.class);
       job.setMapOutputValueClass(BooleanTwoDArrayWritable.class);
       job.setOutputValueClass(Text.class);



        //Sets Mapper and Reducer class.
        job.setMapperClass(SwitchingAlgorithmGenerateGraphMapper.class);
        job.setReducerClass(SwitchingAlgorithmGenerateGraphReducer.class);

       job.addCacheFile(new URI(args[0] + "#" + "mapperfile"));

        return job;

    }

    /**
     *
     * @param stream
     * @return
     */
    private Mapping parseFile(InputStream stream)
    {
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
