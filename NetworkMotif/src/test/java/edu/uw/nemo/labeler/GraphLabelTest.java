/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.labeler;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 *
 * @author vartikav
 */
public class GraphLabelTest {
    @Test
    public void testGraphLabel1() {
       // integration test
        FormatType formatType = FormatType.Graph6;
        int vertexCount = 3;
        GraphLabel graphLabel = new GraphLabel(false);
        
        // graph 1
        List<int[]> edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 1} );
        edges.add( new int[] {0, 2} );
        edges.add( new int[] {1, 2} );
        //graphLabel.addSubGraph(edges, vertexCount);
        
        // graph 2
        edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 1} );
        edges.add( new int[] {1, 2} );
       // graphLabel.addSubGraph(edges, vertexCount);
        
        // graph 3
        edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 2} );
        edges.add( new int[] {1, 2} );
       // graphLabel.addSubGraph(edges, vertexCount);
        
        Map<String, List<Map.Entry<String, Long>>> canonicalLabels = graphLabel.getCanonicalLabels();
        
        assertEquals(canonicalLabels.size(), 2);
        
        // angle
        String expectedCanonicalLabel1 = "BW";
        List<Map.Entry<String, Long>> actualGraphs1 = canonicalLabels.get(expectedCanonicalLabel1);
        int count = 0;
        for (Map.Entry<String, Long> g : actualGraphs1) {
            count += g.getValue();
        }
        
        assertEquals(2, count);
        
        // triangle
        String expectedCanonicalLabel2 = "Bw";
        List<Map.Entry<String, Long>> actualGraphs2 = canonicalLabels.get(expectedCanonicalLabel2);
        count = 0;
        for (Map.Entry<String, Long> g : actualGraphs2) {
            count += g.getValue();
        }
        
        assertEquals(1, count);
    }
    
    @Test
    public void testGraphLabel2() {
        FormatType formatType = FormatType.Graph6;
        int vertexCount = 5;
        GraphLabel graphLabel = new GraphLabel(false);
        
        // graph 1
        List<int[]> edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 3} );
        edges.add( new int[] {0, 4} );
        edges.add( new int[] {1, 2} );
        edges.add( new int[] {2, 4} );
       // graphLabel.addSubGraph(edges, vertexCount);
        
        // graph 2
        edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 1} );
        edges.add( new int[] {0, 3} );
        edges.add( new int[] {1, 4} );
        edges.add( new int[] {2, 4} );
      //  graphLabel.addSubGraph(edges, vertexCount);
        
        // graph 3
        edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 1} );
        edges.add( new int[] {1, 2} );
        edges.add( new int[] {2, 3} );
        edges.add( new int[] {3, 4} );
      //  graphLabel.addSubGraph(edges, vertexCount);
        
        // graph 4
        edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 1} );
        edges.add( new int[] {0, 2} );
        edges.add( new int[] {1, 4} );
        edges.add( new int[] {3, 4} );
     //   graphLabel.addSubGraph(edges, vertexCount);
        
        // graph 5
        edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 2} );
        edges.add( new int[] {0, 4} );
        edges.add( new int[] {1, 2} );
        edges.add( new int[] {3, 4} );
    //    graphLabel.addSubGraph(edges, vertexCount);
        
        Map<String, List<Map.Entry<String, Long>>> canonicalLabels = graphLabel.getCanonicalLabels();
        
        assertEquals(canonicalLabels.size(), 1);
        String expectedCanonicalLabel = "DDW";
        List<Map.Entry<String, Long>> actualGraphs = canonicalLabels.get(expectedCanonicalLabel);
        int count = 0;
        for (Map.Entry<String, Long> g : actualGraphs) {
            count += g.getValue();
        }
        
        assertEquals(5, count);
    }
}
