package edu.uw.nemo;

import org.junit.Test;

import java.util.List;
import java.util.Map;
import static junit.framework.Assert.assertEquals;

public class NemoControllerTest {

    @Test
    public void assertValidMotifCount() {
        NemoController target = new NemoController(/*new Parser(), new ESUGen(), new NautyLabeler(), new DirectCalculator()*/);
      //<String, List<Map.Entry<String, Long>>> actual = target.extract("full_scere_20140427.csv", 4, 1, 0.5);
     //   Map<String, List<Map.Entry<String, Long>>> actual = target.extract("Y2k_GO5_Graphindex.csv", 3, 1000, 0.5);
       // Map<String, List<Map.Entry<String, Long>>> actual = target.extract("Y2k_EB848_Graphindex.csv", 4, 1000, 0.5);
        long startTime = System.currentTimeMillis();
       Map<String, List<Map.Entry<String, Long>>> actual = target.extract("Scere2014Jan_index.csv", 3, 1, 0.5);
        long endTime = System.currentTimeMillis();
        System.out.println("Time taken to determine motfi Significance : " + (endTime - startTime));
         //Map<String, List<Map.Entry<String, Long>>> actual = target.extract("InputGraph1.csv", 3, 3, 0.5);
       // Map<String, List<Map.Entry<String, Long>>> actual = target.extract("Nodes_2000.csv", 3, 100, 0.5);
//        assertEquals(23, actual.size());
    }

}