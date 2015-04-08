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
        
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        
        if (trivialGroupCount == graph.length) {
            return graph.clone();
        }
        
        final GraphSplit gs = split(array1);
        
        long[] result = 
                gs.positiveArray.length < gs.negativeArray.length ?
                        simplifyImpl(gs.positiveArray, gs.negativeArray) :
                        simplifyImpl(gs.negativeArray, gs.positiveArray);
        
        result = append(result, new long[trivialGroupCount]);
        return result;
    }
}
