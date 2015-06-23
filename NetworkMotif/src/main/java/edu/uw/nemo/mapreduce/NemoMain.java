/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

/**
 * Runs a chain of map-reduce jobs to find motifs in a biological network.
 * There are many flags which can be used with it to tweak the performance of the jobs. <br>
 * <b>-randomize:</b> Will randomize output of each iteration of ESU job before running the next iteration. <br>
 * <b>-growReducers:</b> Since the number of sub-graphs grow exponentially in ESU, the number of reducers (and hence mappers) need to also grow with same rate. <br>
 * <b>-setTimeout:</b> Timeout for each mapper/reducer task will be overridden. <br>
 * <b>-setMaxSplitSize:</b> The maximum split size that a mapper process will be set to given value. This helps in further increasing the number of mapper tasks. <br>
 * 
 * @author vartikav
 */
public class NemoMain {

    /**
     * Main entry point
     *
     * @param args args[0]: Input biological network file <br>
     *             args[1]: Intermediate files <br>
     *             args[2]: Final output file <br>
     *             args[3]: labelg executable file path <br>
     *             args[4]: motif size <br>
     *             args[5]: jar file name <br>
     *             args[6+]: -randomize (to randomize the input between iteration of ESU job) <br>
     *             args[6+:x] args[x + 1]: -growReducers <initial count>:<exponent> (to increase the number of reducers with each iteration of ESU job based on the initial count and the exponent) <br>
     *             args[6+:x] args[x + 1]: -setTimeout <value in ms> (to override the timeout value for map-reduce tasks) <br>
     *             args[6+:x] args[x + 1]: -setMaxSplitSize <value in bytes> (to override the max split size for file) <br>
     *             args[6+:x] args[x + 1]: -startStage <value> (to directly start from a later stage. Value can be - 0: ESU, 1: Labeler, 2: Combiner) <br>
     *             args[6+]: -skipG6 (to skip generating g6 format for any sub-graph except in last iteration) <br>
     *             args[6+]: -generateG6InReducer (to move g6 generation in reducer stage of ESU) <br>
     *             args[6+]: -skipEnumeratingSubgraphs (to skip dumping the enumerated subgraphs in final iteration of ESU) <br>
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {

        if (args.length < 6) {
            printUsage();
            return;
        }
        
        boolean randomize = false;
        int initialReducerCount = -1;
        int growthExponent = 1;
        int timeout = -1;
        long maxSplitSize = -1;
        int startStage = 0;
        int esuIterationStart = 0;
        boolean skipG6 = false;
        boolean generateG6InReducer = false;
        boolean skipEnumeratingSubgraphs = false;
        for (int i = 6; i < args.length; i++) {
            if ("-randomize".equals(args[i])) {
                // Will randomize output of each iteration of ESU job
                // before runinng the next iteration
                randomize = true;
            }
            
            if ("-growReducers".equals(args[i])) {
                if (args.length < i + 2) {
                    printUsage();
                    return;
                }
                
                String[] params = args[i + 1].split(":");
                if (params.length != 2) {
                    printUsage();
                    return;
                }
                
                // Since the number of sub-graphs grow exponentially in ESU,
                // the number of reducers (and hence mappers) need to also grow with same rate
                initialReducerCount = Integer.parseInt(params[0]);
                growthExponent = Integer.parseInt(params[1]);
            }
            
            if ("-setTimeout".equals(args[i])) {
                if (args.length < i + 2) {
                    printUsage();
                    return;
                }
                
                // Timeout for each mapper/reducer task will be overridden
                timeout = Integer.parseInt(args[i+1]);
            }
            
            if ("-setMaxSplitSize".equals(args[i])) {
                if (args.length < i + 2) {
                    printUsage();
                    return;
                }
                
                // The maximum split size that a mapper process will be set to given value.
                // This helps in further increasing the number of mapper tasks.
                maxSplitSize = Long.parseLong(args[i+1]);
            }
            
            if ("-startStage".equals(args[i])) {
                if (args.length < i + 2) {
                    printUsage();
                    return;
                }
                
                // To run the pipeline from some specific stage
                String[] stageAndIteration = args[i + 1].split(":");
                startStage = Integer.parseInt(stageAndIteration[0]);
                if (stageAndIteration.length == 2) {
                    esuIterationStart = Integer.parseInt(stageAndIteration[1]);
                }
            }
            
            if ("-skipG6".equals(args[i])) {
                // Will skip generating g6 representation for enumerated sub-graphs
                // in all but last iteration.
                skipG6 = true;
            }
            
            if ("-generateG6InReducer".equals(args[i])) {
                // Instead of generating g6 representation in mapper task,
                // will generate it in reducer task.
                // It could provide slight performance gain because ESU mappers
                // generate duplicate sub-graphs which are de-duped in reducer task.
                generateG6InReducer = true;
            }
            
            if ("-skipEnumeratingSubgraphs".equals(args[i])) {
                // Will not enumerate sub-graphs in last iteration of ESU to reduce
                // the size of intermediate data, thus reducing the disk IO operations and hence the performance
                skipEnumeratingSubgraphs = true;
            }
        }

        String biologicalNetworkFile = args[0];
        String intermediateFile = args[1];
        String finalOutputFile = args[2];
        String labelgFile = args[3];
        String motifSize = args[4];
        String jarFileName = args[5];

        // job 1: ESU iterative mapreduce  
        ESUJob esuJob = new ESUJob(randomize, initialReducerCount, growthExponent, timeout, maxSplitSize, skipG6, generateG6InReducer, skipEnumeratingSubgraphs, esuIterationStart);
        String[] esuArgs = new String[4];
        esuArgs[0] = biologicalNetworkFile;
        esuArgs[1] = intermediateFile + "/ESU";
        esuArgs[2] = motifSize;
        esuArgs[3] = jarFileName;
        if (startStage <= 0) esuJob.run(esuArgs);

        // job 2: labelling
        int labelerReducerCount = Integer.parseInt(motifSize) < 4 ? -1 : initialReducerCount * (int)(Math.pow(growthExponent, Integer.parseInt(motifSize) - 4));
        LabelerJob labelerJob = new LabelerJob(skipEnumeratingSubgraphs, labelerReducerCount);
        String[] labelerArgs = new String[4];
        labelerArgs[0] = labelgFile;
        labelerArgs[1] = esuArgs[1] + (Integer.parseInt(motifSize) - 1);
        labelerArgs[2] = intermediateFile + "/Labeler";
        labelerArgs[3] = jarFileName;
        if (startStage <= 1) labelerJob.run(labelerArgs);

        // job 3: aggregate canonical label counting
        CombinerJob combinerJob = new CombinerJob();
        String[] combinerArgs = new String[4];
        combinerArgs[0] = labelerArgs[2];
        combinerArgs[1] = finalOutputFile;
        combinerArgs[2] = intermediateFile + "/Combiner";
        combinerArgs[3] = jarFileName;
        if (startStage <= 2) combinerJob.run(combinerArgs);
    }
    
    private static void printUsage() {
        System.out.println("Incorrect Usage. Pass the corect arguments (v3).");
        System.out.println("Usage:");
        System.out.println("args[0]: Input biological network file");
        System.out.println("args[1]: Intermediate files");
        System.out.println("args[2]: Final output file");
        System.out.println("args[3]: labelg executable file path");
        System.out.println("args[4]: motif size");
        System.out.println("args[5]: jar file name");
        System.out.println("[optional] args[6+]: -randomize (to randomize the input between iteration of ESU job)");
        System.out.println("[optional] args[6+:x] args[x + 1]: -growReducers <initial count>:<exponent> (to increase the number of reducers with each iteration of ESU job based on the initial count and the exponent)");
        System.out.println("[optional] args[6+:x] args[x + 1]: -setTimeout <value in ms> (to override the timeout value for map-reduce tasks)");
        System.out.println("[optional] args[6+:x] args[x + 1]: -setMaxSplitSize <value in bytes> (to override the max split size for file)");
        System.out.println("[optional] args[6+:x] args[x + 1]: -startStage <value> (to directly start from a later stage. Value can be - 0: ESU, 1: Labeler, 2: Combiner)");
        System.out.println("[optional] args[6+]: -skipG6 (to skip generating g6 format for any sub-graph except in last iteration)");
        System.out.println("[optional] args[6+]: -generateG6InReducer (to move g6 generation in reducer stage of ESU)");
        System.out.println("[optional] args[6+]: -skipEnumeratingSubgraphs (to skip dumping the enumerated subgraphs in final iteration of ESU)");
    }
}