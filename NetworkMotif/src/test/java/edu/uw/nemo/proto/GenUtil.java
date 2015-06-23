package edu.uw.nemo.proto;

import java.util.ArrayList;
import java.util.List;

public class GenUtil {
    public GenUtil() {
    }

    List<int[]> setupAL() {
        List<int[]> input = new ArrayList<int[]>();
        input.add(new int[]{1, 2});
        input.add(new int[]{1, 3});
        input.add(new int[]{1, 4});
        input.add(new int[]{1, 5});
        input.add(new int[]{2, 3});
        input.add(new int[]{2, 6});
        input.add(new int[]{2, 7});
        input.add(new int[]{3, 8});
        input.add(new int[]{3, 9});
        return input;
    }

    void printCombinations(List<int[]> actual) {
        for (int i = 0; i < actual.size(); i++) {
            int[] cur = actual.get(i);
            int length = cur.length;
            for (int j = 0; j < length; j++) {
                System.out.print(cur[j]);
                if (j < length - 1) {
                    System.out.print(" -> ");
                }
            }
            System.out.println("");
        }
        System.out.println("\ntotal [" + actual.size() + "] elements.");
    }

    public void print(byte[] actual) {
        for (int j = 0; j < actual.length; j++) {
            System.out.print(actual[j] + " ");
        }
        System.out.println("");
    }
}