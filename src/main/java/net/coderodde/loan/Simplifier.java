package net.coderodde.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.coderodde.loan.support.GeneralPartitionGenerator;
import net.coderodde.loan.support.ReversedGeneralPartitionGenerator;
import net.coderodde.loan.support.SpecialPartitionGenerator;

/**
 * This class defines the API for loan graph simplifiers. A loan graph is
 * represented by an array of long values, where each component corresponds to a
 * node in the graph, and the value of the component corresponds to the equity 
 * of the node.
 * <p>
 * A <b>group</b> is a set of nodes, for which the sum of equities is zero. A 
 * group of one node with equity 0 is called a <b>trivial group</b>. A group of
 * two nodes with opposite non-zero equities is called <b>semi-trivial</b>.
 * <p>
 * A group <tt>G</tt> is called a <b>proper group</b> if and only if <tt>G</tt> 
 * cannot be partitioned in two non-empty groups.
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
    
    protected static final int SEMITRIVIAL_GROUPS_INDEX = 0;
    protected static final int NONTRIVIAL_GROUPS_INDEX = 1;
    
    /**
     * The comparator for sorting the groups.
     */
    private static final NodeListComparator nodeListComparator = 
            new NodeListComparator();
    
    /**
     * <code>cachedPositiveArray[i]</code> caches the sum of positive equities
     * from the block <tt>i</tt>.
     */
    private long[] cachedPositiveArray;
    
    /**
     * <code>cachedNegativeArray[i]</code> caches the sum of negative equities
     * from the block <tt>i</tt>.
     */
    private long[] cachedNegativeArray;
    
    /**
     * Constructs this simplifier and initializes the cache arrays.
     */
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
    
    /**
     * Holds a partition of a graph into array of positive nodes and an array
     * of negative nodes.
     */
    protected static class GraphSplit {
        public final long[] positiveArray;
        public final long[] negativeArray;
        
        GraphSplit(final long[] positiveArray, final long[] negativeArray) {
            this.positiveArray = positiveArray;
            this.negativeArray = negativeArray;
        }
    }
    
    /**
     * Splits the input graph into positive and negative nodes.
     * 
     * @param  graph the graph to split.
     * @return a graph split.
     */
    protected static GraphSplit split(final long[] graph) {
        int positiveCount = 0;
        int negativeCount = 0;
        
        for (int i = 0; i < graph.length; ++i) {
            final long l = graph[i];
            
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
        
        for (final long l : graph) {
            if (l > 0L) {
                positiveArray[positiveIndex++] = l;
            } else {
                negativeArray[negativeIndex++] = l;
            }
        }
        
        return new GraphSplit(positiveArray, 
                              negativeArray);
    }
    
    /**
     * Counts the amount of groups formed by the indices. 
     * <code>positiveIndices[i]</code> gives the block index for a node
     * <code>positiveArray[i]</code>. Negative structures work in analogous way.
     * Once the nodes are in their blocks, the routine checks whether they can
     * be matched. If so the amount of blocks is returned. Otherwise zero is 
     * returned indicating that the buckets may not be paired into groups.
     * 
     * @param positiveArray   the array of positive nodes.
     * @param negativeArray   the array of negative nodes.
     * @param positiveIndices the array of positive indices.
     * @param negativeIndices the array of negative indices.
     * @param k               the amount of partition blocks.
     * @return                the amount of groups in the data.
     */
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
    
    /**
     * Reconstructs a solution from the input data.
     * 
     * @param positiveArray   the array of positive nodes.
     * @param negativeArray   the array of negative nodes.
     * @param positiveIndices the array of positive indices.
     * @param negativeIndices the array of negative indices.
     * @param blocks          the amount of partition blocks.
     * @return                the graph with <code>blocks</code> groups.
     */
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
    
    /**
     * Implements the algorithm for group maximization.
     * 
     * @param  smallArray the smaller of the node arrays.
     * @param  largeArray the larger of the node arrays.
     * 
     * @return the node array producing maximal amount of groups.
     */
    protected long[] simplifyImpl(final long[] smallArray,
                                  final long[] largeArray) {
        return simplifyImpl(smallArray, largeArray, 1);
    }
    
    /**
     * Implements the algorithm for group maximization.
     * 
     * @param  smallArray    the smaller of the node arrays.
     * @param  largeArray    the larger of the node arrays.
     * @param  initialBlocks the amount of initial blocks.
     * 
     * @return the node array producing maximal amount of groups.
     */
    protected long[] simplifyImpl(final long[] smallArray,
                                  final long[] largeArray,
                                  final int initialBlocks) {
        final GeneralPartitionGenerator smallGenerator =
                new GeneralPartitionGenerator(smallArray.length,
                                              initialBlocks);
        
        final int[] bestSmallIndices = new int[smallArray.length];
        final int[] bestLargeIndices = new int[largeArray.length];
        
        int bestGroupAmount = 0;
        int bestk = -1;
        
        do {
            final int[] smallIndices = smallGenerator.getIndices();
            final int blocks = smallGenerator.getk();
            
            final SpecialPartitionGenerator largeGenerator = 
                    new SpecialPartitionGenerator(largeArray.length, blocks);
            
            final int[] largeIndices = largeGenerator.getIndices();
            
            do {
                int groups = countGroups(smallArray,
                                         largeArray,
                                         smallIndices,
                                         largeIndices,
                                         blocks);
                
                if (bestGroupAmount < groups) {
                    bestGroupAmount = groups;
                    bestk = blocks;
                    
                    System.arraycopy(smallIndices, 
                                     0, 
                                     bestSmallIndices,
                                     0,
                                     smallIndices.length);
                    
                    System.arraycopy(largeIndices,
                                     0, 
                                     bestLargeIndices, 
                                     0, 
                                     largeIndices.length);
                }
            } while (largeGenerator.inc());
        } while (smallGenerator.inc());
        
        return buildSolution(smallArray,
                             largeArray,
                             bestSmallIndices,
                             bestLargeIndices,
                             bestk);
    }
    
    /**
     * Implements the algorithm for group maximization.
     * 
     * @param  smallArray    the smaller of the node arrays.
     * @param  largeArray    the larger of the node arrays.
     * @param  minimumBlocks the minimum amount of blocks.
     * 
     * @return the node array producing maximal amount of groups.
     */
    protected long[] simplifyImplReversed(final long[] smallArray,
                                          final long[] largeArray,
                                          final int minimumBlocks) {
        final ReversedGeneralPartitionGenerator smallGenerator =
                new ReversedGeneralPartitionGenerator(smallArray.length,
                                                      minimumBlocks);
        do {
            final int[] smallIndices = smallGenerator.getIndices();
            
            final int blocks = smallGenerator.getk();
            
            final SpecialPartitionGenerator largeGenerator = 
                    new SpecialPartitionGenerator(largeArray.length, blocks);
            
            final int[] largeIndices = largeGenerator.getIndices();
            
            do {
                int groups = countGroups(smallArray,
                                         largeArray,
                                         smallIndices,
                                         largeIndices,
                                         blocks);
                
                if (groups > 0) {
                    return buildSolution(smallArray,
                                         largeArray,
                                         smallIndices,
                                         largeIndices,
                                         blocks);
                }
            } while (largeGenerator.inc());
        } while (smallGenerator.inc());
        
        throw new IllegalStateException("Should not get here.");
    }
    
    /**
     * Splits the input graph into to arrays: one containing only semi-trivial 
     * groups, and another one containing non-trivial groups.
     * 
     * @param  graph the graph from which to extract the semi-trivial groups.
     * @return       two arrays: one for semi-trivial nodes, and another for 
     *               non-trivial groups.
     */
    protected static long[][] stripSemitrivialGroups(final long[] graph) {
        final Map<Long, Integer> map = new HashMap<>();
        
        for (final long l : graph) {
            if (!map.containsKey(l)) {
                map.put(l, 1);
            } else {
                map.put(l, map.get(l) + 1);
            }
        }
        
        final List<Long> semitrivialNodeList = new ArrayList<>(graph.length);
        final List<Long> nontrivialNodeList = new ArrayList<>(graph.length);
        
        for (final long l : graph) {
            if (map.containsKey(l) && map.containsKey(-l)) {
                final int minOccurrences = Math.min(map.get(l), map.get(-l));
                
                for (int i = 0; i < minOccurrences; ++i) {
                    semitrivialNodeList.add(l);
                    semitrivialNodeList.add(-l);
                }
                
                map.put(l, map.get(l) - minOccurrences);
                map.put(-l, map.get(-l) - minOccurrences);
                
                if (map.get(l) == 0) {
                    map.remove(l);
                }
                
                if (map.get(-l) == 0) {
                    map.remove(-l);
                }
            } else if (map.containsKey(l)) {
                for (int i = 0; i < map.get(l); ++i) {
                    nontrivialNodeList.add(l);
                }
                
                map.remove(l);
            } else if (map.containsKey(-l)){
                for (int i = 0; i < map.get(-l); ++i) {
                    nontrivialNodeList.add(-l);
                }
                
                map.remove(-l);
            }
        }
        
        final long[] semitrivialArray = new long[semitrivialNodeList.size()];
        final long[] nontrivialArray = new long[nontrivialNodeList.size()];
        
        int index = 0;
        
        for (final long l : semitrivialNodeList) {
            semitrivialArray[index++] = l;
        }
        
        index = 0;
        
        for (final long l : nontrivialNodeList) {
            nontrivialArray[index++] = l;
        }
            
        final long[][] ret = new long[2][];
        
        ret[SEMITRIVIAL_GROUPS_INDEX] = semitrivialArray;
        ret[NONTRIVIAL_GROUPS_INDEX] = nontrivialArray;
        
        return ret;
    }
    
    /**
     * Creates and returns an array resulting from concatenating 
     * <code>array1</code> and <code>array2</code>.
     * 
     * @param  array1 the first array.
     * @param  array2 the second array to append to the first one.
     * @return the concatenation of the two input arrays.
     */
    protected static long[] append(final long[] array1, final long[] array2) {
        final long[] ret = new long[array1.length + array2.length];
        System.arraycopy(array1, 0, ret, 0, array1.length);
        System.arraycopy(array2, 0, ret, array1.length, array2.length);
        return ret;
    }
}
