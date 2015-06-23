package edu.uw.nemo.motifSignificant;

import edu.uw.nemo.labeler.GraphFormat;
import edu.uw.nemo.motifSignificant.explicitMethod.RandomGraphCanonicalLabelling;

import java.text.DecimalFormat;
import java.util.*;

 /**
  * This class calculates the statistically significant motif by computing Z-score and p-value
  * If Z-score > 2.0 or p-value <0.01 then it is said to be statistically significant.
 * Created by Zalak on 4/25/2015.
 */
public class CalculateMotifSignificance
{
    /**
     * The frequency of subgraph pattern is calculate from the given probability of vertices.
     * The function below estimates its 100 percent frequency
     * @param count : frequency of subgraph pattern found in a random graph
     * @param prob : percentage of vertex taken into consideration
     * @return
     */
    long estimateCount100Percent(long count, double prob)
    {
        return (long)((count * 1.0)/prob);
    }

    /**
     * Calculate the toal frquency of the subgraph pattern found in random graph
     * @param graphLabel : G6 representation of subgraph pattern found in random graph
     * @param prob : percentage of vertices taken into consideration for enumerating subgraphs
     * @return
     */
    HashMap<String, Long> mapLabeltoCount(Map<String, List<Map.Entry<String, Long>>> graphLabel, double prob)
    {
        HashMap<String, Long> labelMapping = new HashMap<String, Long>();

        for (Map.Entry<String, List<Map.Entry<String, Long>>> e : graphLabel.entrySet())
        {
            long count =0;
            if(labelMapping.containsKey(e.getKey()))
            {
                count = labelMapping.get(e.getKey());
            }

            Map<String, Long> subGraphCounts = GraphFormat.countDistinctGraphs(e.getValue());

            for (Map.Entry<String, Long> c : subGraphCounts.entrySet())
            {
                count += c.getValue();
            }

            //Estimate the 100 percent frequency
            if(prob < 1.0 && prob > 0.0)
            {
                count = estimateCount100Percent(count, prob);
            }
            labelMapping.put(e.getKey(), count);
        }

        return labelMapping;
    }


    /**
     * Print the significant motif in input graph by calculating Z-score or p-value
     * @param randomGraphLabelList : list of random graph mapping object
     * @param inputGraphLabel Linput graph mapping objects
     * @param prob : percentage of vertices taken into consideration for enumerating subgraphs
     * @param totalRandomGraph : number of random graphs generated
     * @param k : size of subgraph
     */
    public void printSignificantMotif(ArrayList<RandomGraphCanonicalLabelling>randomGraphLabelList, Map<String, List<Map.Entry<String, Long>>> inputGraphLabel, double prob, int totalRandomGraph, int k)
    {
        HashMap<String, Long> inputGraphLabelCount = mapLabeltoCount(inputGraphLabel, 1.0);
        System.out.println("input Graph " +  "\n");
        print(inputGraphLabelCount);
        int index =0;
        for(RandomGraphCanonicalLabelling randomGraphLabel : randomGraphLabelList)
        {
            randomGraphLabel.labelCountMapping = mapLabeltoCount(randomGraphLabel.canonicalSubgraphs, prob);
            System.out.println("Random Graph " + index++ + "\n");
            print(randomGraphLabel.labelCountMapping);
        }

        // if number of random graph generated >= 1000 then calculate p-value
        if(totalRandomGraph >= 1000)
        {
              calculatePValue(randomGraphLabelList, inputGraphLabelCount, totalRandomGraph, k);
        }
        // number of random graphs generated < 1000
        else
        {
            calculateZScore(randomGraphLabelList, inputGraphLabelCount,totalRandomGraph, k);
        }

    }


    /**
     * Calculate the mean frequency of a subgraph pattern found in N random graphs
     * @param randomGraphLabelList : list of random graph mapping objects generated
     * @param motif : subgraph pattern label
     * @param N : number of random graph generated
     * @return
     */
    double calculateMean(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, String motif, int N)
    {
        long count = 0;
        for (RandomGraphCanonicalLabelling randomGraphLabel : randomGraphLabelList)
        {
            if(randomGraphLabel.labelCountMapping.containsKey(motif))
            {
                count += randomGraphLabel.labelCountMapping.get(motif);
            }
        }
        return ((double)count/(double)N);
    }

    /**
     * Calculate the standard devaition of a subgraph pattern
     * @param randomGraphLabelList : list of random graph mapping objects generated
     * @param motif : subgraph pattern label
     * @param mean : the mean frequency of a subgraph pattern found in N random graphs
     * @param N : number of random graph generated
     * @return
     */
    double calculateStandardDeviation(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, String motif, double mean, int N)
    {
        double sum = 0;
        double x=0;
        for (RandomGraphCanonicalLabelling randomGraphLabel : randomGraphLabelList)
        {
            if(randomGraphLabel.labelCountMapping.containsKey(motif))
            {
                double difference = randomGraphLabel.labelCountMapping.get(motif) - mean;
                x +=  Math.pow(difference, 2);
            }
        }
        double variance = (double)(x)/(double)(N);
        double sqrt = Math.sqrt(variance);

        DecimalFormat df = new DecimalFormat("#.###");
        return Double.valueOf(df.format(sqrt));
    }

    /**
     * Calculates the Z-score for each subgraph pattern found in input graph
     * @param randomGraphLabelList : list of random graph mapping objects generated
     * @param inputGraphLabelCount : list of k-size subgraph patterns found in input graph and its frequency
     * @param N : number of random graph generated
     * @param k : size of subgraph
     */
   private void calculateZScore(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, HashMap<String, Long> inputGraphLabelCount, int N, int k)
    {

        HashMap<String, Double> labelZscore = new HashMap<String, Double>();
        //For each subgraph pattern found in input graph calculate its Z-score
        for(Map.Entry<String, Long> inputGraph : inputGraphLabelCount.entrySet())
        {
            //mean frequency
            double mean = calculateMean(randomGraphLabelList, inputGraph.getKey(), N);
            //standard deviation
            double stdDeviation = calculateStandardDeviation(randomGraphLabelList, inputGraph.getKey(), mean, N);

            double difference = inputGraph.getValue() - mean;
            double zscore = (difference / stdDeviation);

            labelZscore.put(inputGraph.getKey(), zscore);
        }

        //compares the Z-scoe of each subgraph pattern.
        //If Z-score > 2.0 then that subgraph pattern is significant.
        for(Map.Entry<String, Double> label : labelZscore.entrySet())
        {
            System.out.println("Z-Score for motif "  + label.getKey() + "for size " + k + "is:  " + label.getValue());
            if(label.getValue() >= 2.0)
            {
                System.out.println("Significant Motif for size " + k + " subgraph is " + label.getKey());
                System.out.println("Z-Score for motif " + label.getKey() + " is " + label.getValue());
            }
        }

    }

    /**
     * Calculates the p-value for each subgraph pattern found in input graph
     * @param randomGraphLabelList : list of random graph mapping objects generated
     * @param inputGraphLabelCount : list of k-size subgraph patterns found in input graph and its frequency
     * @param N : number of random graph generated
     * @param k : size of subgraph
     */
    private void calculatePValue(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, HashMap<String, Long> inputGraphLabelCount, int N, int k)
    {
        int cn = 0;
        HashMap<String, Integer> labelCount = new HashMap<String, Integer>();
        for(Map.Entry<String, Long> inputGraph : inputGraphLabelCount.entrySet())
        {
            cn = 0;
            for(RandomGraphCanonicalLabelling randomGraphList : randomGraphLabelList)
            {
                for(Map.Entry<String, Long> randomGraph : randomGraphList.labelCountMapping.entrySet())
                {
                    // for each subgraph pattern
                    if(inputGraph.getKey().equals(randomGraph.getKey()))
                    {
                        //if frequency of subgraph pattern found in a random graph > then frequency of subgraph pattern found in input graph then increment the count
                        if(randomGraph.getValue() >= inputGraph.getValue())
                        {
                            cn++;
                        }
                    }
                }
            }
            labelCount.put(inputGraph.getKey(), cn);
        }


        for(Map.Entry<String, Integer> label : labelCount.entrySet())
        {
          //divide the total number of random graphs whose frequency of a subgraph pattern > then frequency of a subgraph pattern found in input graph by N
            double pvalue = (double)label.getValue()/(double)N;
            System.out.println("p-value for motif "  + label.getKey() + "for size " + k + "is:  " + label.getValue());

            //If p-value < 0.01 then that subgraph pattern is significant.
            if(pvalue < 0.01)
            {
                System.out.println("Significant Motif for size " + k + " subgraph is " + label.getKey());
                System.out.println("p-value for motif " + label.getKey() + " is " + label.getValue());
            }
        }

    }

    /**
     *
     * @param graphLabelcount
     */
    void print(HashMap<String, Long> graphLabelcount)
    {
       for(Map.Entry<String, Long> key: graphLabelcount.entrySet())
        {
            System.out.println(key.getKey() + " : " + key.getValue());
        }
    }
}
