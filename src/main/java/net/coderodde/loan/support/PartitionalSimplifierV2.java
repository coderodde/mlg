package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;

/**
 * This simplifier improves 
 * {@link net.coderodde.loan.support.PartitionalSimplifierV1} by resolving 
 * semi-trivial groups before proceeding to generating partitions.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class PartitionalSimplifierV2 extends Simplifier {
    
    @Override
    public long[] simplify(long[] graph) {
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        
        if (trivialGroupCount == graph.length) {
            return graph.clone();
        }
        
        final GraphSplit gs = split(array1);
        
        final long[] result = 
                gs.positiveArray.length < gs.negativeArray.length ?
                        simplifyImpl(gs.positiveArray, gs.negativeArray) :
                        simplifyImpl(gs.negativeArray, gs.positiveArray);
        
        if (trivialGroupCount > 0) {
            final long[] ret = new long[graph.length];
            System.arraycopy(result, 0, ret, 0, result.length);
            return ret;
        } else {
            return result;
        }
    }
}
