package edu.uw.nemo.motifSignificant;

import edu.uw.nemo.motifSignificant.explicitMethod.RandomGraphCanonicalLabelling;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by Zalak on 5/15/2015.
 */
public class CalculateMotifSignificanceTest {

    @Test
    public void testEstimateCount100Percent() throws Exception {
        CalculateMotifSignificance cm = new CalculateMotifSignificance();
        assertTrue(200== cm.estimateCount100Percent(140, 0.7));

    }

    @Test
    public void testMapLabeltoCount() throws Exception {

    }

    @Test
    public void testPrintSignificantMotif() throws Exception {

    }

    @Test
    public void testCalculateMean() throws Exception {

    }


    @Test
    public void testCalculateStandardDeviation() throws Exception {
        CalculateMotifSignificance cm = new CalculateMotifSignificance();
        RandomGraphCanonicalLabelling lbl[] = new RandomGraphCanonicalLabelling[20];
        ArrayList<RandomGraphCanonicalLabelling> lblList = new ArrayList<RandomGraphCanonicalLabelling>();
        for(int i = 0; i < 20; i++) {
            lbl[i] = new RandomGraphCanonicalLabelling(null, null);
        }

            lbl[0].labelCountMapping.put("BW", 9L);
            lbl[1].labelCountMapping.put("BW", 2L);
            lbl[2].labelCountMapping.put("BW", 5L);
            lbl[3].labelCountMapping.put("BW", 4L);
            lbl[4].labelCountMapping.put("BW", 12L);
            lbl[5].labelCountMapping.put("BW", 7L);
            lbl[6].labelCountMapping.put("BW", 8L);
            lbl[7].labelCountMapping.put("BW", 11L);
            lbl[8].labelCountMapping.put("BW", 9L);
            lbl[9].labelCountMapping.put("BW", 3L);
            lbl[10].labelCountMapping.put("BW", 7L);
            lbl[11].labelCountMapping.put("BW", 4L);
            lbl[12].labelCountMapping.put("BW", 12L);
            lbl[13].labelCountMapping.put("BW", 5L);
            lbl[14].labelCountMapping.put("BW", 4L);
            lbl[15].labelCountMapping.put("BW", 10L);
            lbl[16].labelCountMapping.put("BW", 9L);
            lbl[17].labelCountMapping.put("BW", 6L);
            lbl[18].labelCountMapping.put("BW", 9L);
            lbl[19].labelCountMapping.put("BW", 4L);
        for(int i= 0; i< 20; i++){
            lblList.add(lbl[i]);
        }



        assertTrue(7F == cm.calculateMean(lblList, "BW", 20));
        assertTrue(2.983 == cm.calculateStandardDeviation(lblList, "BW", 7, 20));



    }

    @Test
    public void testPrint() throws Exception {

    }
}