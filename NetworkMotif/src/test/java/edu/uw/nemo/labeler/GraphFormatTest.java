/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.labeler;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

/**
 *
 * @author vartikav
 */
public class GraphFormatTest {
    @Test
    public void testGraph6Format() {
        FormatType formatType = FormatType.Graph6;
        int vertexCount = 5;
        List<int[]> edges = new ArrayList<int[]>();
        edges.add( new int[] {0, 2} );
        edges.add( new int[] {0, 4} );
        edges.add( new int[] {1, 3} );
        edges.add( new int[] {3, 4} );
        
        GraphFormat graphFormat = new GraphFormat(formatType, edges, vertexCount, false);
        assertEquals("DQc", graphFormat.toString());
    }
}
