package net.coderodde.loan.support;

import java.util.ArrayList;
import java.util.List;
import net.coderodde.loan.Simplifier;
import static net.coderodde.loan.Utilities.checkIsGroup;

/**
 * This simplifier improves 
 * {@link net.coderodde.loan.support.CombinatorialSimplifierV2} by using 
 * concurrency.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class CombinatorialSimplifierV3 extends Simplifier {

    @Override
    public long[] simplify(long[] graph) {
        checkIsGroup(graph);
        
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final Simplifier.GroupSplit gs = split(graph);
        
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
            groupList = simplifyV3(initialList);
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
}
