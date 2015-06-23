package edu.uw.nemo.motifSignificant.mapreduce;


import java.io.IOException;
import java.net.URISyntaxException;




/**
 * Created by Zalak on 5/5/2015.
 */
public class NemoRandomGraphMain
{
    public static void main(String args[]) throws IOException, InterruptedException, URISyntaxException, ClassNotFoundException {
        long startTime = System.currentTimeMillis();

        System.out.println(args.length);

        if(args.length < 7)
        {
            System.out.println("Invalid Input: ");

            return;
        }
        String biologicalNetworkFile = args[0];
        String outputFile = args[1];
        String totalRandomGraph = args[2];
        if(Double.valueOf(args[3]) < 0.0 || Double.valueOf(args[3])> 1.0)
        {
            System.out.println("Invalid probability: probability should be within 0 and 1");
            return;
        }
        String probability = args[3];
        String jarFileName = args[4];
        String logDir = args[5];
        String size = args[6];


        for(int i= 0; i< args.length; i++)
        {
            System.out.println(args[i]);
        }


        GraphGeneratorJob graphGeneratorJob = new GraphGeneratorJob();
        String jobArgs[] = new String[args.length];
        jobArgs[0] = biologicalNetworkFile;
        jobArgs[1]= outputFile;
        jobArgs[2] = totalRandomGraph;
        jobArgs[3]= probability;
        jobArgs[4]= jarFileName;
        jobArgs[5] = logDir;
        jobArgs[6] = size;
        graphGeneratorJob.run(jobArgs);


        long stopTime = System.currentTimeMillis();
        long elapsedTime = stopTime - startTime;

        System.out.println("Elapsed Time: " + elapsedTime);


    }
}
