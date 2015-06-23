package edu.uw.nemo.motifSignificant.mapreduce;

import org.apache.hadoop.io.BooleanWritable;
import org.apache.hadoop.io.ByteWritable;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.TwoDArrayWritable;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Zalak on 5/9/2015.
 */
public class BooleanTwoDArrayWritable extends TwoDArrayWritable
{
    public BooleanTwoDArrayWritable() {
        super(BooleanWritable.class);
        //super(IntWritable.class);
    }

    public void write(DataOutput out) throws IOException {
        System.out.println("Zalak Write Started");
        super.write(out);
        System.out.println("Zalak Write Ended");
    }

}
