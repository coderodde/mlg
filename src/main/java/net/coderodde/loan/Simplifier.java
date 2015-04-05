package net.coderodde.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This class defines the API for loan graph simplifiers. A loan graph is
 * represented by an array of long values, where each component corresponds to a
 * node in the graph, and the value of the component corresponds to the equity 
 * of the node.
 * <p>
 * A <b>group</b> is a set of nodes, for which the sum of equities is zero. A 
 * group of one node with equity 0 is called a <b>trivial group</b>. A group 
 * <tt>G</tt> is called a <b>proper group</b> if and only if <tt>G</tt> cannot
 * be partitioned in two non-empty groups.
 * <p>
 * Since we want to minimize the amount of arcs in the loan graph, the problem
 * may be rephrased as the problem of splitting the graph in as many groups as
 * possible. Given a loan graph <tt>G</tt>, it can be reconnected with 
 * <tt>|G| - k</tt> arcs, if <tt>G</tt> is split in <tt>k</tt> groups.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public abstract class Simplifier {
    
    private static final NodeListComparator nodeListComparator = 
            new NodeListComparator();
    
    private long[] cachedPositiveArray;
    private long[] cachedNegativeArray;
    
    public Simplifier() {
        this.cachedPositiveArray = new long[0];
        this.cachedNegativeArray = new long[0];
    }
    
    /**
     * Simplifies the input graph using a particular algorithm.
     * 
     * @param  graph the graph to simplify.
     * @return a simplified graph.
     */
    public abstract long[] simplify(final long[] graph);
    
    /**
     * Returns the same graph, but with trivial groups removed.
     * 
     * @param  graph the graph to rid of zero elements.
     * @return a copy of <code>graph</code> without trivial groups.
     */
    protected static long[] stripTrivialGroups(final long[] graph) {
        int trivialGroups = 0;
        
        for (final long l : graph) {
            if (l == 0L) {
                ++trivialGroups;
            }
        }
        
        if (trivialGroups == 0) {
            return graph.clone();
        }
        
        final long[] ret = new long[graph.length - trivialGroups];
        
        int index = 0;
        
        for (final long l : graph) {
            if (l != 0L) {
                ret[index++] = l;
            }
        }
        
        return ret;
    }
    
    protected static class GraphSplit {
        public final long[] positiveArray;
        public final long[] negativeArray;
        
        GraphSplit(final long[] positiveArray, final long[] negativeArray) {
            this.positiveArray = positiveArray;
            this.negativeArray = negativeArray;
        }
    }
    
    protected static GraphSplit split(final long[] array) {
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (int i = 0; i < array.length; ++i) {
            final long l = array[i];
            
            if (l < 0L) {
                ++negativeCount;
            } else if (l > 0L) {
                ++positiveCount;
            } else {
                throw new IllegalArgumentException(
                        "Zero element at index " + i + ".");
            }
        }
        
        final long[] positiveArray = new long[positiveCount];
        final long[] negativeArray = new long[negativeCount];
        
        int positiveIndex = 0;
        int negativeIndex = 0;
        
        for (final long l : array) {
            if (l > 0L) {
                positiveArray[positiveIndex++] = l;
            } else {
                negativeArray[negativeIndex++] = l;
            }
        }
        
        return new GraphSplit(positiveArray, 
                              negativeArray);
    }
    
    protected int countGroups(final long[] positiveArray,
                              final long[] negativeArray,
                              final int[] positiveIndices,
                              final int[] negativeIndices,
                              final int k) {
        if (cachedPositiveArray.length != k) {
            cachedPositiveArray = new long[k];
            cachedNegativeArray = new long[k];
        } else {
            for (int i = 0; i < k; ++i) {
                cachedPositiveArray[i] = 0L;
                cachedNegativeArray[i] = 0L;
            }
        }
        
        for (int i = 0; i < positiveIndices.length; ++i) {
            cachedPositiveArray[positiveIndices[i]] += positiveArray[i];
        }
        
        for (int i = 0; i < negativeIndices.length; ++i) {
            // cachedNegativeArray[i] is the absolute value of the sum of 
            // negative elements in the block.
            cachedNegativeArray[negativeIndices[i]] -= negativeArray[i]; 
        }
        
        Arrays.sort(cachedPositiveArray);
        Arrays.sort(cachedNegativeArray);
        
        for (int i = 0; i < cachedPositiveArray.length; ++i) {
            if (cachedPositiveArray[i] != cachedNegativeArray[i]) {
                return 0;
            }
        }
        
        return k;
    }
    
    protected long[] buildSolution(final long[] positiveArray,
                                   final long[] negativeArray,
                                   final int[] positiveIndices,
                                   final int[] negativeIndices,
                                   final int blocks) {
        final List<Long>[] positiveListArray = new ArrayList[blocks];
        final List<Long>[] negativeListArray = new ArrayList[blocks];
        
        for (int i = 0; i < blocks; ++i) {
            positiveListArray[i] = new ArrayList<>();
            negativeListArray[i] = new ArrayList<>();
        }
        
        final long[] ret = new long[positiveArray.length + 
                                    negativeArray.length];
        
        for (int i = 0; i < positiveIndices.length; ++i) {
            positiveListArray[positiveIndices[i]].add(positiveArray[i]);
        }
        
        for (int i = 0; i < negativeIndices.length; ++i) {
            negativeListArray[negativeIndices[i]].add(negativeArray[i]);
        }
        
        Arrays.sort(positiveListArray, nodeListComparator);
        Arrays.sort(negativeListArray, nodeListComparator);
        
        int index = 0;
        
        for (int i = 0; i < blocks; ++i) {
            final List<Long> positiveList = positiveListArray[i];
            final List<Long> negativeList = negativeListArray[i];
            
            for (final long l : positiveList) {
                ret[index++] = l;
            }
            
            for (final long l : negativeList) {
                ret[index++] = l;
            }
        }
        
        return ret;
    }
    
    private static final class NodeListComparator 
    implements Comparator<List<Long>> {

        @Override
        public int compare(final List<Long> o1, final List<Long> o2) {
            return Long.compare(abssum(o1), abssum(o2));
        }
        
        private long abssum(final List<Long> o1) {
            long sum = 0L;
            
            for (final Long l : o1) {
                sum += l;
            }
            
            return Math.abs(sum);
        }
    }
}
