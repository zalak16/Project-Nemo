package edu.uw.nemo.proto;

import java.util.*;

/**
 * Created by joglekaa on 5/14/14.
 */
public class MatrixGen {

    public byte[] graphToAdjacencyMatrix(List<int[]> adj, int[] motif) {
        Map<Integer, Set> adjMap = listToMap(adj);
        int length = motif.length;
        byte[] rslt = new byte[length * length];
        for (int row = 0; row < length; row++) {
            int rowId = motif[row] - 1;
            for (int col = row + 1; col < length; col++) {
                int colId = motif[col] - 1;
                if (isConnected(adjMap, rowId + 1, colId + 1)) {
                    rslt[rowId * length + colId] = 1;
                    rslt[colId * length + rowId] = 1;
                }
            }
        }
        return rslt;
    }

    private boolean isConnected(Map<Integer, Set> adjMap, int rowId, int colId) {
        Set set = adjMap.get(rowId);
        return set != null && set.contains(colId);
    }

    private Map<Integer, Set> listToMap(List<int[]> adj) {
        HashMap<Integer, Set> rslt = new HashMap<Integer, Set>();
        for (int[] edge : adj) {
            Set val = rslt.get(edge[0]);
            if (val == null) {
                val = new HashSet<Integer>();
                rslt.put(edge[0], val);
            }
            val.add(edge[1]);
            Set val2 = rslt.get(edge[1]);
            if (val2 == null) {
                val2 = new HashSet<Integer>();
                rslt.put(edge[1], val2);
            }
            val2.add(edge[0]);
        }
        return rslt;
    }


}
