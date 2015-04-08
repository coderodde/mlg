package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import static net.coderodde.loan.Utilities.checkIsGroup;

/**
 * This simplifier implements a simplification algorithm, which splits the input
 * graph into two lists: a negative and a positive list. It then chooses the
 * smaller of them and splits it in all possible partitions. For each partition
 * of the latter list, the algorithm generates all possible 
 * <tt>k</tt>-partitions of the larger set, where <tt>k</tt> is the amount of 
 * blocks in the partition of the smaller set. 
 * 
 * @author Rodion Efremov
 * @version  1.6
 */
public class PartitionalSimplifierV1 extends Simplifier {

    @Override
    public long[] simplify(long[] graph) {
        checkIsGroup(graph);
        
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final GroupSplit gs = split(graph);
        
        if (gs.trivialGroups.length == graph.length) {
            return graph.clone();
        }
        
        final GraphSplit gs2 = splitBySign(append(gs.nontrivialGroups, 
                                            gs.semitrivialGroups));
        
        long[] result = gs2.positiveArray.length < gs2.negativeArray.length ?
                        simplifyImpl(gs2.positiveArray, gs2.negativeArray) :
                        simplifyImpl(gs2.negativeArray, gs2.positiveArray);
        
        
        result = append(result, gs.trivialGroups);
        return result;
    }
}
