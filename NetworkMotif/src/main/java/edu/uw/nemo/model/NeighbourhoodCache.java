/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.uw.nemo.model;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

/**
 * This class holds the cached neighbourhoods for sub-graphs to fasten the sub-graph enumeration task.
 * @author vartikav
 */
public class NeighbourhoodCache {
    
    /**
     * The cache key for the Neighbourhood cache.
     * It is basically unordered list of vertices which constitute the sub-graph.
     * Thus, {1, 2, 3} is same as {2, 3, 1} and have the same neighbourhood.
     */
    private class CacheKey {
        private List<Integer> keys;
        
        /**
         * Create the cache key for the given subgraph
         * @param keys The given subgraph
         */
        public CacheKey(SubGraph keys) {
            this.keys = new ArrayList<Integer>(keys.size());
            for (int i = 0; i < keys.size(); i++) {
                this.keys.add(keys.get(i));
            }
        }
        
        @Override
        public boolean equals(Object obj) {
            if (obj == null || !(obj instanceof CacheKey)) {
                return false;
            }
            
            if (obj == this) {
                return true;
            }
            
            CacheKey objAsType = (CacheKey)obj;
            if (this.keys.size() != objAsType.keys.size()) {
                return false;
            }
            
            return this.keys.containsAll(objAsType.keys);
        }
        
        @Override
        public int hashCode() {
            int hashCode = 0;
            for (Integer key : this.keys) {
                hashCode ^= key.hashCode();
            }
            
            return hashCode;
        }
    }
    
    private final int MaxCacheSize = 5;
    private Map<CacheKey, List<AdjacentVertexWithEdge>> cachedNeighborhoods;
    private Queue<CacheKey> lruQueue;
    
    private final boolean disable;
    
    /**
     * Constructs the neighbourhood cache.
     * @param disable If true, the neighbourhood cache won't be populated. Otherwise it will be.
     */
    public NeighbourhoodCache(boolean disable) {
        this.disable = disable;
        this.cachedNeighborhoods = new HashMap<CacheKey, List<AdjacentVertexWithEdge>>(MaxCacheSize);
        this.lruQueue = new ArrayDeque<CacheKey>(MaxCacheSize);
    }
    
    /**
     * Returns the cached neighbourhood for the given subgraph.
     * @param subGraph The subgraph for which neighbourhood need to be looked up.
     * @return The cached neighbourhood if in cache, otherwise null.
     */
    public List<AdjacentVertexWithEdge> getNeighbourhood(SubGraph subGraph) {
        if (disable) {
            return null;
        }
        
        CacheKey cacheKey = new CacheKey(subGraph);
        List<AdjacentVertexWithEdge> neighbourhood = cachedNeighborhoods.get(cacheKey);
        if (neighbourhood != null) {
            // moved cached key to the end of the queue
            lruQueue.remove(cacheKey);
            lruQueue.add(cacheKey);
        }
        
        return neighbourhood;
    }
    
    /**
     * Adds the neighbourhood to the cache.
     * @param subGraph The subgraph for which the neighbourhood to be cached.
     * @param neighbourhood The neighbourhood for the subgraph.
     */
    public void addNeighbourhood(SubGraph subGraph, List<AdjacentVertexWithEdge> neighbourhood) {
        if (disable) {
            return;
        }
        
        CacheKey cacheKey = new CacheKey(subGraph);
        if (this.cachedNeighborhoods.size() == MaxCacheSize) {
            CacheKey cachedKeyToRemove = this.lruQueue.remove();
            this.cachedNeighborhoods.remove(cachedKeyToRemove);
        }
        
        this.cachedNeighborhoods.put(cacheKey, neighbourhood);
        this.lruQueue.add(cacheKey);
    }
}
