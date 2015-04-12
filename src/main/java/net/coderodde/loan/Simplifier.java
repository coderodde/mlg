package net.coderodde.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static net.coderodde.loan.Utilities.isGroup;
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
    
    /**
     * Specifies the minimum amount of nodes to process in the parallel 
     * combinatorial simplifier.
     */
    private static final int MINIMUM_THREAD_LOAD = 10;
    
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
     * Holds a partition of a graph to trivial, semi-trivial and non-trivial
     * groups.
     */
    protected static class GroupSplit {
        public final long[] trivialGroups;
        public final long[] semitrivialGroups;
        public final long[] nontrivialGroups;
        
        GroupSplit(final long[] trivialGroups,
                   final long[] semitrivialGroups,
                   final long[] nontrivialGroups) {
            this.trivialGroups = trivialGroups;
            this.semitrivialGroups = semitrivialGroups;
            this.nontrivialGroups = nontrivialGroups;
        }
    }
    
    /**
     * Splits the input graph into positive and negative nodes.
     * 
     * @param  graph the graph to split.
     * @return a graph split.
     */
    protected static GraphSplit splitBySign(final long[] graph) {
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
    
    /**
     * This class implements a group comparator using the absolute value of the
     * sums as a comparison key.
     */
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
     * Implements the combinatorial search for groups.
     * 
     * @param  list the node list to partition.
     * @return the list of groups.
     */
    protected List<List<Long>> simplify(final List<Long> list) {
        final boolean[] flags = new boolean[list.size()];
        final long combinationsToConsider = mypow(2L, flags.length) - 2L;
        
        flags[0] = true;
        int bestGroupCount = 0;
        final List<List<Long>> totalGroupList = new ArrayList<>();
        
        // Generate all ways of splitting the input list into two sublists.
        for (long l = 0L; l < combinationsToConsider; ++l, incFlags(flags)) {
            final List<Long>[] lists = split(list, flags);
            
            if (isGroup(lists[0]) && isGroup(lists[1])) {
                final List<List<Long>> groupList0 = simplify(lists[0]);
                final List<List<Long>> groupList1 = simplify(lists[1]);
                final int groupCount = groupList0.size() + groupList1.size();
                
                if (bestGroupCount < groupCount) {
                    bestGroupCount = groupCount;
                    totalGroupList.clear();
                    totalGroupList.addAll(groupList0);
                    totalGroupList.addAll(groupList1);
                }
            }
        }
        
        if (totalGroupList.isEmpty()) {
            totalGroupList.add(new ArrayList<>(list));
        }
        
        return totalGroupList;
    }
    
    /**
     * Implements the combinatorial search for groups.
     * 
     * @param  list the node list to partition.
     * @return the list of groups.
     */
    protected List<List<Long>> simplifyV2(final List<Long> list) {
        final boolean[] flags = new boolean[list.size()];
        final long combinationsToConsider = mypow(2L, flags.length - 1) - 1L;
        
        flags[0] = true;
        int bestGroupCount = 0;
        final List<List<Long>> totalGroupList = new ArrayList<>();
        
        // Generate all ways of splitting the input list into two sublists.
        for (long l = 0L; l < combinationsToConsider; ++l, incFlags(flags)) {
            final List<Long>[] lists = split(list, flags);
            
            if (isGroup(lists[0]) && isGroup(lists[1])) {
                final List<List<Long>> groupList0 = simplify(lists[0]);
                final List<List<Long>> groupList1 = simplify(lists[1]);
                final int groupCount = groupList0.size() + groupList1.size();
                
                if (bestGroupCount < groupCount) {
                    bestGroupCount = groupCount;
                    totalGroupList.clear();
                    totalGroupList.addAll(groupList0);
                    totalGroupList.addAll(groupList1);
                }
            }
        }
        
        if (totalGroupList.isEmpty()) {
            totalGroupList.add(new ArrayList<>(list));
        }
        
        return totalGroupList;
    }
    
    protected class CombinatorialSimplifierThread extends Thread {
        
        /**
         * The amount of most-significant bits to skip.
         */
        private final int skip;
        
        /**
         * The input list.
         */
        private final List<Long> input;
        
        /**
         * The output list consisting of groups.
         */
        private final List<List<Long>> output;
        
        /**
         * Creates a new simplification thread. Relies on combinatorial 
         * approach.
         * 
         * @param input the input list. Must be a group.
         * @param skip  the amount of most-significant bits to ignore.
         */
        CombinatorialSimplifierThread(final List<Long> input, final int skip) {
            this.input = input;
            this.skip = skip;
            this.output = new ArrayList<>();
        }
        
        /**
         * Runs the actual simplification.
         */
        @Override
        public void run() {
            final boolean[] flags = new boolean[input.size()];
            final long combinationsToConsider = 
                    mypow(2L, flags.length - skip - 1) - 1L;

            flags[0] = true;
            int bestGroupCount = 0;
            final List<List<Long>> totalGroupList = new ArrayList<>();

            // Generate all ways of splitting the input list into two sublists.
            for (long l = 0L; l < combinationsToConsider; ++l, incFlags(flags)) {
                final List<Long>[] lists = split(input, flags);

                if (isGroup(lists[0]) && isGroup(lists[1])) {
                    final List<List<Long>> groupList0 = simplify(lists[0]);
                    final List<List<Long>> groupList1 = simplify(lists[1]);
                    final int groupCount = groupList0.size() + groupList1.size();

                    if (bestGroupCount < groupCount) {
                        bestGroupCount = groupCount;
                        totalGroupList.clear();
                        totalGroupList.addAll(groupList0);
                        totalGroupList.addAll(groupList1);
                    }
                }
            }

            if (totalGroupList.isEmpty()) {
                output.add(new ArrayList<>(input));
            }
        }
        
        public List<List<Long>> getResult() {
            return output;
        }
    }
    
    protected List<List<Long>> simplifyV3(final List<Long> list) {
        final int coreAmount = Runtime.getRuntime().availableProcessors();
        
        if (coreAmount < 2) {
            return simplifyV2(list);
        }
        
        final int powerOfTwo = ceilToPowerOfTwo(coreAmount);
        final int logCoreAmount = intLog2(coreAmount);
        
        if (list.size() - 1 - logCoreAmount < MINIMUM_THREAD_LOAD) {
            return simplifyV2(list);
        }
        
        System.out.println("Threads: " + powerOfTwo);
        final CombinatorialSimplifierThread[] threads = 
                new CombinatorialSimplifierThread[powerOfTwo];
        return null;
    }
    
    /**
     * Ceils up <code>num</code> to the nearest power of two.
     * 
     * @param  num the number to ceil.
     * @return nearest power of two equal or larger than <code>num</code>.
     */
    protected static int ceilToPowerOfTwo(final int num) {
        int s = 1;
        
        while (s < num) {
            s <<= 1;
        }
        
        return s;
    }
    
    /**
     * Returns a base 2 logarithm, ceiled towards the nearest integer.
     * 
     * @param  num the number whose logarithm to compute.
     * @return     logarithm of <code>num</code>.
     */
    protected static int intLog2(final int num) {
        int s = 1;
        
        while (s < num) {
            s <<= 1;
        }
        
        return Integer.numberOfTrailingZeros(s);
    }
    
    /**
     * Splits the list in two sublists. If <code>flags[i] == true</code>, the
     * element <code>list.get(i)</code> will go one list, if 
     * <code>flags[i] == false</code>, it will go to another.
     * 
     * @param list  the list to split.
     * @param flags the flags.
     * @return      two lists.
     */
    protected static List<Long>[] split(final List<Long> list, 
                                        final boolean[] flags) {
        final List<Long> lista = new ArrayList<>(list.size());
        final List<Long> listb = new ArrayList<>(list.size());
        
        for (int i = 0; i < flags.length; ++i) {
            (flags[i] ? lista : listb).add(list.get(i));
        }
        
        return new List[]{lista, listb};
    }
    
    /**
     * Increments the integer represented by <code>flags</code>.
     * 
     * @param flags the flag structure to increment.
     */
    protected static void incFlags(final boolean[] flags) {
        for (int i = 0; i < flags.length; ++i) {
            if (flags[i]) {
                flags[i] = false;
            } else {
                flags[i] = true;
                return;
            }
        }
    }
    
    /**
     * Returns <code>a</code> raised to the power of <code>b</code>.
     * 
     * @param  a the number.
     * @param  b the exponent.
     * @return <code>a^b</code>.
     */
    protected static long mypow(final long a, final long b) {
        long tmp = 1L;
        
        for (long i = 0; i < b; ++i) {
            tmp *= a;
        }
        
        return tmp;
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
     * Implements the algorithm for group maximization. Works in reversed 
     * fashion: the first match is guaranteed to be optimal.
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
            final int[] smallArrayIndices = smallGenerator.getIndices();
            
            final int blocks = smallGenerator.getk();
            
            final SpecialPartitionGenerator largeGenerator = 
                    new SpecialPartitionGenerator(largeArray.length, blocks);
            
            final int[] largeArrayIndices = largeGenerator.getIndices();
            
            do {
                int groups = countGroups(smallArray,
                                         largeArray,
                                         smallArrayIndices,
                                         largeArrayIndices,
                                         blocks);
                
                if (groups > 0) {
                    return buildSolution(smallArray,
                                         largeArray,
                                         smallArrayIndices,
                                         largeArrayIndices,
                                         blocks);
                }
            } while (largeGenerator.inc());
        } while (smallGenerator.inc());
        
        throw new IllegalStateException("Should not get here.");
    }
    
    /**
     * Splits the input graph into trivial, semi-trivial and non-trivial groups.
     * 
     * @param  graph the graph to split.
     * @return the graph partition.
     */
    protected static GroupSplit split(final long[] graph) {
        int trivialGroupCount = 0;
        
        for (final long l : graph) {
            if (l == 0L) {
                ++trivialGroupCount;
            }
        }
        
        final long[] trivialGroups = new long[trivialGroupCount];
        final Map<Long, Integer> map = new HashMap<>();
        
        for (final long l : graph) {
            if (l == 0L) {
                // Skip trivial groups.
                continue;
            }
            
            if (!map.containsKey(l)) {
                map.put(l, 1);
            } else {
                map.put(l, map.get(l) + 1);
            }
        }
        
        final List<Long> semitrivialNodeList = 
                new ArrayList<>(graph.length - trivialGroupCount);
        
        final List<Long> nontrivialNodeList = 
                new ArrayList<>(graph.length - trivialGroupCount);
        
        for (final long l : graph) {
            if (l == 0L) {
                // Skip trivial group once again.
                continue;
            }
            
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
        
        final long[] semitrivialGroups = new long[semitrivialNodeList.size()];
        final long[] nontrivialGroups = new long[nontrivialNodeList.size()];
        
        int index = 0;
        
        for (final long l : semitrivialNodeList) {
            semitrivialGroups[index++] = l;
        }
        
        index = 0;
        
        for (final long l : nontrivialNodeList) {
            nontrivialGroups[index++] = l;
        }
            
        final long[][] ret = new long[2][];
        
        return new GroupSplit(trivialGroups, 
                              semitrivialGroups, 
                              nontrivialGroups);
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
    
    /**
     * Sums those elements in <code>list</code> whose index is mentioned in
     * <code>indices</code>.
     * 
     * @param  list    the list of elements.
     * @param  indices the indices of elements to select.
     * @return the sum of selected elements.
     */
    protected static long sum(final List<Long> list, final int[] indices) {
        long sum = 0L;
        
        for (final int i : indices) {
            sum += list.get(i);
        }
        
        return sum;
    }
}
