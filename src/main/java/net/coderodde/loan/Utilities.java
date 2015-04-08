package net.coderodde.loan;

import java.util.List;
import java.util.Random;

/**
 * This class contains some basic utilities.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class Utilities {
    
    /**
     * The {@link print} routine will not print an array larger than this 
     * integer.
     */
    private static final int MAX_GRAPH_SIZE_FOR_PRINT = 40;
    
    /**
     * The maximum percentage for arc load factor.
     */
    private static final float MAX_ARC_LOAD_FACTOR = 0.99f;
    
    /**
     * Creates a random loan graph represented by an array of node equities.
     * 
     * @param length         the amount of nodes in the graph.
     * @param random         the random number generator.
     * @param minWeight      the minimum weight of an arc.
     * @param maxWeight      the maximum weight of an arc.    
     * @param arcLoadFactor the percentage of the amount of edges to add to the
     *                       graph.
     * @return               a random loan graph.
     */
    public static final long[] createEquityArray(final int length,
                                                 final Random random,
                                                 final long minWeight,
                                                 final long maxWeight,
                                                 final float arcLoadFactor) {
        final long[] array = new long[length];
        
        int arcs = (int)(length * (length - 1) 
                                * Math.min(arcLoadFactor, 
                                           MAX_ARC_LOAD_FACTOR));
        
        while (arcs > 0) {
            final long weight = 
                    minWeight + (long)((maxWeight - minWeight) 
                                        * random.nextFloat());
            final int a = random.nextInt(length);
            final int b = random.nextInt(length);
            
            array[a] += weight;
            array[b] -= weight;
            
            --arcs;
        }
        
        return array;
    }
    
    /**
     * Checks that <code>array</code> is a group. A group is an array of (long)
     * integers such that their sum is zero.
     * 
     * @param  array the group candidate.
     * @return <code>true</code> if <code>array</code> is a group.
     */
    public static final boolean isGroup(final long[] array) {
        long sum = 0L;
        
        for (final long l : array) {
            sum += l;
        }
        
        return sum == 0L;
    }
    
    /**
     * Checks that the input list is a group.
     * 
     * @param  list the list to check.
     * @return <code>true</code> if <code>list</code> is a group.
     */
    public static final boolean isGroup(final List<Long> list) {
        long sum = 0L;
        
        for (final long l : list) {
            sum += l;
        }
        
        return sum == 0L;
    }
    
    /**
     * Counts the amount of integer substrings summing to zero.
     * 
     * @param  array the array to process.
     * @return the amount of substrings summing to zero.
     */
    public static int countGroups(final long[] array) {
        if (!isGroup(array)) {
            throw new IllegalArgumentException(
                    "The input graph is not a group.");
        }
        
        long sum = 0L;
        int count = 0;
        
        for (final long node : array) {
            if (node == 0L) {
                ++count;
                continue;
            }
            
            sum += node;
            
            if (sum == 0L) {
                ++count;
            }
        }
        
        return count;
    }
    
    /**
     * Prints a graph if it is not too large.
     * 
     * @param graph the graph to print.
     */
    public static void print(final long[] graph) {
        if (graph.length <= MAX_GRAPH_SIZE_FOR_PRINT) {
            for (final long l : graph) {
                System.out.print(l + " ");
            }
            
            System.out.println();
        }
    }
    
    /**
     * Checks that <code>graph</code> is a group, and if it is not, throws an
     * exception.
     * 
     * @param graph the graph to check.
     * @exception IllegalArgumentException if the input graph is not a group.
     */
    public static void checkIsGroup(final long[] graph) {
        if (!isGroup(graph)) {
            throw new IllegalArgumentException(
                    "The input graph is not a group.");
        }
    }
}
