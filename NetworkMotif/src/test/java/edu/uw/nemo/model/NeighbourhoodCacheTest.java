/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.model;

import java.util.*;
import org.junit.Test;

import static junit.framework.Assert.*;
import org.junit.Assert;

/**
 *
 * @author vartikav
 */
public class NeighbourhoodCacheTest {
    @Test
    public void testCachingNeighbourhood() {
        SubGraph subGraph = new SubGraph(2, false);
        subGraph.add(1);
        subGraph.add(2);
        
        List<AdjacentVertexWithEdge> neighbourhood = new ArrayList<AdjacentVertexWithEdge>();
        neighbourhood.add(new AdjacentVertexWithEdge(3, 1));
        neighbourhood.add(new AdjacentVertexWithEdge(4, 1));
        neighbourhood.add(new AdjacentVertexWithEdge(5, 1));
        neighbourhood.add(new AdjacentVertexWithEdge(8, 2));
        
        NeighbourhoodCache cache = new NeighbourhoodCache(false);
        cache.addNeighbourhood(subGraph, neighbourhood);
        
        SubGraph newSubGraph = new SubGraph(2, false);
        newSubGraph.add(2);
        newSubGraph.add(1);
        
        List<AdjacentVertexWithEdge> newNeighbourhood = cache.getNeighbourhood(newSubGraph);
        
        assertNotNull(newNeighbourhood);
        assertEquals(neighbourhood.size(), newNeighbourhood.size());
        for (AdjacentVertexWithEdge node : neighbourhood) {
            assertTrue(newNeighbourhood.contains(node));
        }
    }
}
