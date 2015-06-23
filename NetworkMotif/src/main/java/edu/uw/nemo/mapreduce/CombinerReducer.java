/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.mapreduce;

import java.io.IOException;

import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/**
 * Collects the canonical labels and compute the aggregated count for them.
 *
 * @author vartikav
 */
public class CombinerReducer extends Reducer<Text, Text, Text, Text> {

    /**
     * Computes the total count of a canonical label along with list of
     * corresponding graphs
     *
     * @param key Canonical Label
     * @param values List of graphs corresponding to the canonical label with
     * their count
     * @param context
     * @throws IOException
     * @throws InterruptedException
     */
    @Override
    protected void reduce(Text key, Iterable<Text> values, Context context)
            throws IOException, InterruptedException {
        // read the canonical label
        String canonicalLabel = key.toString().trim();

        // compute the total sum for this canonical label
        long sum = 0;
        StringBuilder valueBldr = new StringBuilder();
        for (Text value : values) {
            valueBldr.append(value.toString());
            valueBldr.append(",");
            String[] g6AndCount = value.toString().split("#");
            sum += Long.parseLong(g6AndCount[1].trim());
        }

        valueBldr.insert(0, sum + ";");

        // write the final output
        // <canonical label><TAB><total count><SEMI-COLON>[List of comma separated <g6 label><PIPE(|)><count>]
        context.write(new Text(canonicalLabel), new Text(valueBldr.substring(0, valueBldr.length() - 1)));
    }
}
