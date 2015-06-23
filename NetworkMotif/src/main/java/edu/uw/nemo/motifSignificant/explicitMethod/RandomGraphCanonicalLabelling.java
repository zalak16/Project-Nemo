package edu.uw.nemo.motifSignificant.explicitMethod;



import edu.uw.nemo.model.Mapping;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

/**
 * Created by Zalak on 4/8/2015.
 */
public class RandomGraphCanonicalLabelling {

    public Mapping map;
    public Map<String, List<Map.Entry<String, Long>>> canonicalSubgraphs;
    public HashMap<String, Long> labelCountMapping;
    public HashMap<String, Double>labelSubgraphConcentration;


    public RandomGraphCanonicalLabelling(Mapping map, Map<String, List<Map.Entry<String, Long>>> canonicalSubgraphs)
    {
        this.map = map;
        this.canonicalSubgraphs = canonicalSubgraphs;
        this.labelCountMapping = new HashMap<String, Long>();
        this.labelSubgraphConcentration = new HashMap<String, Double>();
    }


}
