package net.coderodde.loan.support;

import java.util.ArrayList;
import java.util.List;
import net.coderodde.loan.Simplifier;

/**
 * This simplifier seeks to divide the array in two subarrays in such a way,
 * that each one is a group. Then both subarrays are attempted to be split again
 * and so on.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class CombinatorialSimplifier extends Simplifier {
    
    private List<List<Long>> groupList;
    
    public CombinatorialSimplifier() {}
    
    private CombinatorialSimplifier(final List<List<Long>> groupList) {
        this.groupList = groupList;
    }
    
    @Override
    public long[] simplify(long[] graph) {
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        
        // If the graph consists of only trivial groups, return.
        if (trivialGroupCount == graph.length) {
            return graph.clone();
        }
        
        // Remove semi-trivial groups.
        final long[][] data1 = stripSemitrivialGroups(array1);
        
        // BEGIN: create the input list.
        final List<Long> initialList = 
                new ArrayList<>(data1[NONTRIVIAL_GROUPS_INDEX].length);
        
        for (final Long l : data1[NONTRIVIAL_GROUPS_INDEX]) {
            initialList.add(l);
        }
        // END: create the input list.
        
        final Data data2 = initialList.isEmpty() ? 
                           new Data() : 
                           simplify(initialList);
        
        int index = 0;
        
        long[] result = 
                new long[graph.length - trivialGroupCount 
                                      - data1[SEMITRIVIAL_GROUPS_INDEX].length];
        
        for (final List<Long> list : data2.groupList) {
            for (final long l : list) {
                result[index++] = l;
            }
        }
        
        result = append(result, data1[SEMITRIVIAL_GROUPS_INDEX]);
        
        if (trivialGroupCount > 0) {
            result = append(result, new long[trivialGroupCount]);
        } 
        
        return result;
    } 
    
    private static final class Data {
        final List<List<Long>> groupList;
        
        Data() {
            this.groupList = new ArrayList<>();
        }
    }
    
    private Data simplify(final List<Long> list) {
        if (list.isEmpty()) {
            return new Data();
        }
        
        if (!isGroup(list)) {
            // The input list cannot be a group.
            throw new IllegalStateException(
                    "Should not happen: input list is not a group.");
        }
        
        final boolean[] flags = new boolean[list.size()];
        final long combinationsToConsider = mypow(2L, flags.length) - 2L;
        
        flags[0] = true;
        int bestGroupCount = 0;
        final Data ret = new Data();
        
        // Generate all ways of splitting the input list into two sublists.
        for (long l = 0L; l < combinationsToConsider; ++l, incFlags(flags)) {
            final List<Long>[] lists = split(list, flags);
            
            if (isGroup(lists[0]) && isGroup(lists[1])) {
                final Data data0 = simplify(lists[0]);
                final Data data1 = simplify(lists[1]);
                final int groupCount = data0.groupList.size() + 
                                       data1.groupList.size();
                
                if (bestGroupCount < groupCount) {
                    bestGroupCount = groupCount;
                    ret.groupList.clear();
                    ret.groupList.addAll(data0.groupList);
                    ret.groupList.addAll(data1.groupList);
                }
            }
        }
        
        if (ret.groupList.isEmpty()) {
            final List<Long> retList = new ArrayList<>(list);
            ret.groupList.add(retList);
        }
        
        return ret;
    }
    
    private static List<Long>[] split(final List<Long> list, 
                                      final boolean[] flags) {
        final List<Long> lista = new ArrayList<>(list.size());
        final List<Long> listb = new ArrayList<>(list.size());
        
        for (int i = 0; i < flags.length; ++i) {
            (flags[i] ? lista : listb).add(list.get(i));
        }
        
        return new List[]{lista, listb};
    }
    
    private static boolean isGroup(final List<Long> list) {
        long sum = 0L;
        
        for (final long l : list) {
            sum += l;
        }
        
        return sum == 0L;
    }
    
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
    
    protected static long mypow(final long a, final long b) {
        long tmp = 1L;
        
        for (long i = 0; i < b; ++i) {
            tmp *= a;
        }
        
        return tmp;
    }
}
