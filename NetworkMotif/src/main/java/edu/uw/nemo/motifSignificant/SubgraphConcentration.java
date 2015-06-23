package edu.uw.nemo.motifSignificant;

import edu.uw.nemo.labeler.GraphFormat;
import edu.uw.nemo.motifSignificant.explicitMethod.RandomGraphCanonicalLabelling;
import org.apache.hadoop.util.hash.Hash;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Zalak on 5/17/2015.
 */
public class SubgraphConcentration
{
    long estimateCount100Percent(long count, double prob)
    {
        return (long)((count * 1.0)/prob);
    }

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

            if(prob < 1.0 && prob > 0.0)
            {
                count = estimateCount100Percent(count, prob);
            }
            labelMapping.put(e.getKey(), count);
        }
        return labelMapping;
    }

    HashMap<String, Double> calculateSubgraphConcentration(HashMap<String, Long> labelCount)
    {
        HashMap<String, Double> labelSubgraphConcentration = new HashMap<String, Double>();
        long nSubgraph = totalSubgraph(labelCount);
        for(Map.Entry<String, Long> label : labelCount.entrySet())
        {
            double concentration = (double)label.getValue() / (double)nSubgraph;
              labelSubgraphConcentration.put(label.getKey(), concentration);
        }

        return labelSubgraphConcentration;
    }

    private long totalSubgraph(HashMap<String, Long> labelCount)
    {
        long nSubgraph = 0;
        for(Map.Entry<String, Long> subgraph : labelCount.entrySet())
        {
            nSubgraph += subgraph.getValue();
        }
        return nSubgraph;
    }



    public void printSignificantMotif(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, Map<String, List<Map.Entry<String, Long>>> inputGraphLabel, int totalRandomGraph, int k, double prob)
    {
        HashMap<String, Long> inputGraphLabelCount = mapLabeltoCount(inputGraphLabel, 1.0);
        HashMap<String, Double>inputGraphSubgraphConcentration = calculateSubgraphConcentration(inputGraphLabelCount);

        System.out.println("input Graph " +  "\n");
        printCount(inputGraphLabelCount);
        print(inputGraphSubgraphConcentration);

        int index =0;
        for(RandomGraphCanonicalLabelling randomGraphLabel : randomGraphLabelList)
        {
            randomGraphLabel.labelCountMapping = mapLabeltoCount(randomGraphLabel.canonicalSubgraphs, prob);

            randomGraphLabel.labelSubgraphConcentration = calculateSubgraphConcentration(randomGraphLabel.labelCountMapping);

            System.out.println("Random Graph " + index++ + "\n");
            printCount(randomGraphLabel.labelCountMapping);
            print(randomGraphLabel.labelSubgraphConcentration);
        }

           calculateZScore(randomGraphLabelList, inputGraphSubgraphConcentration,totalRandomGraph, k);
    }

    private void calculateZScore(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, HashMap<String, Double> inputGraphSubgraphConcentration, int N, int k)
    {
        HashMap<String, Double> labelZscore = new HashMap<String, Double>();

       for(Map.Entry<String,Double> inputGraph : inputGraphSubgraphConcentration.entrySet())
       {
            double mean = calculateMean(randomGraphLabelList, inputGraph.getKey(), N);
            double stdDeviation = calculateStandardDeviation(randomGraphLabelList, inputGraph.getKey(), mean, N);
          // System.out.println(mean + " : " + stdDeviation);

            double difference = inputGraph.getValue() - mean;
           double zscore;
           if(stdDeviation != 0.0) {
               zscore = (difference / stdDeviation);
           }
           else
           {
               zscore = 0;
           }
        //   System.out.println(zscore);

            System.out.println("Mean Subgraph Concentration: " + inputGraph.getKey() + " : " + mean);
            labelZscore.put(inputGraph.getKey(), zscore);
        }

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

    private double calculateStandardDeviation(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, String motif, double mean, int N)
    {
        double sum = 0;
        double x=0;
        for (RandomGraphCanonicalLabelling randomGraphLabel : randomGraphLabelList)
        {
            if(randomGraphLabel.labelSubgraphConcentration.containsKey(motif))
            {
                double difference = randomGraphLabel.labelSubgraphConcentration.get(motif) - mean;
                double pow = Math.pow(difference,2);
                x +=  Math.pow(difference, 2);
                //System.out.println("difference is " + difference + "power is " + pow + "x is " + x);
            }
        }
        double variance = (double)(x)/(double)(N);
        //System.out.println("variance: " + variance);
        double sqrt = Math.sqrt(variance);
        //System.out.println("sqrt: " + sqrt);

       return sqrt;

    }

    private double calculateMean(ArrayList<RandomGraphCanonicalLabelling> randomGraphLabelList, String key, int N) {

        double count = 0;
        for(RandomGraphCanonicalLabelling randomGraph : randomGraphLabelList)
        {
            if(randomGraph.labelSubgraphConcentration.containsKey(key))
            {
                count += randomGraph.labelSubgraphConcentration.get(key);
            }
        }
        return (count/(double)N);
    }

    private void printCount(HashMap<String, Long> labelCount)
    {
        for(Map.Entry<String, Long> subgraphCount : labelCount.entrySet())
        {
            System.out.println(subgraphCount.getKey() + " : " + subgraphCount.getValue());
        }
    }

    private void print(HashMap<String, Double> labelSubgraphConcentration)
    {
        for(Map.Entry<String, Double> subgraphConcentration : labelSubgraphConcentration.entrySet())
        {
            System.out.println(subgraphConcentration.getKey() + " : " + subgraphConcentration.getValue());
        }

    }
}
