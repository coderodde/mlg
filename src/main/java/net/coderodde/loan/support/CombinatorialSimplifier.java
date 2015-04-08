package net.coderodde.loan.support;

import java.util.ArrayList;
import java.util.List;
import net.coderodde.loan.Simplifier;
import static net.coderodde.loan.Utilities.checkIsGroup;

/**
 * This simplifier seeks to divide the array in two subarrays in such a way,
 * that each one is a group. Then both subarrays are attempted to be split again
 * and so on.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class CombinatorialSimplifier extends Simplifier {
    
    @Override
    public long[] simplify(long[] graph) {
        checkIsGroup(graph);
        
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final GroupSplit gs = split2(graph);
        
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        
        // If the graph consists of only trivial groups, return.
        if (gs.trivialGroups.length == graph.length) {
            return graph.clone();
        }
        
        // BEGIN: create the input list.
        final List<Long> initialList = 
                new ArrayList<>(gs.nontrivialGroups.length);
        
        for (final Long l : gs.nontrivialGroups) {
            initialList.add(l);
        }
        // END: create the input list.
        
        List<List<Long>> groupList;
        long[] result = new long[gs.nontrivialGroups.length];
        
        if (!initialList.isEmpty()) {
            groupList = simplify(initialList);
            int index = 0;

            for (final List<Long> list : groupList) {
                for (final long l : list) {
                    result[index++] = l;
                }
            }
        }
        
        result = append(result, gs.trivialGroups);
        result = append(result, gs.semitrivialGroups);
        return result;
    } 
    
    private static final class Data {
        final List<List<Long>> groupList;
        
        Data() {
            this.groupList = new ArrayList<>();
        }
    }
    
    private List<List<Long>> simplify(final List<Long> list) {
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
