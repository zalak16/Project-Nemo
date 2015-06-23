/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.uw.nemo.model;

import org.junit.Test;

import static junit.framework.Assert.*;

/**
 *
 * @author vartikav
 */
public class SubGraphTest {
    @Test
    public void testToString() {
        SubGraph subGraph = new SubGraph(3, false);
        subGraph.add(1);
        subGraph.add(0);
        subGraph.add(2);
        
        assertEquals("0,1,2", subGraph.toString());
        
        subGraph.clear();
        subGraph.add(2);
        subGraph.add(1);
        subGraph.add(0);
        
        assertEquals("0,1,2", subGraph.toString());
        
        subGraph = new SubGraph(4, true);
        subGraph.add(2);
        subGraph.add(1);
        subGraph.add(0);
        subGraph.add(6);
        
        assertEquals("0,1,2,6", subGraph.toString());
        subGraph.remove();
        assertEquals("0,1,2", subGraph.toString());
        subGraph.remove();
        assertEquals("1,2", subGraph.toString());
        
        subGraph = new SubGraph(1, true);
        subGraph.add(1);
              
        assertEquals("1", subGraph.toString());
        
        subGraph = new SubGraph(4, false);
        subGraph.add(18);
        subGraph.add(9);
        subGraph.add(3);
        subGraph.add(90);
        
        assertEquals("3,9,18,90", subGraph.toString());
        subGraph.remove();
        assertEquals("3,9,18", subGraph.toString());
        subGraph.remove();
        assertEquals("9,18", subGraph.toString());
    }    
}
