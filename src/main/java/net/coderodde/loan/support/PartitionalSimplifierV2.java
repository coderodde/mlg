package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import static net.coderodde.loan.Utilities.checkIsGroup;

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
        checkIsGroup(graph);
        
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        
        if (trivialGroupCount == graph.length) {
            return graph.clone();
        }
        
        final long[][] data = stripSemitrivialGroups(array1);
        final GraphSplit gs = split(data[NONTRIVIAL_GROUPS_INDEX]);
        
        long[] result = new long[0];
        
        if (gs.positiveArray.length > 0) {
            result = gs.positiveArray.length < gs.negativeArray.length ?
                        simplifyImpl(gs.positiveArray, gs.negativeArray) :
                        simplifyImpl(gs.negativeArray, gs.positiveArray);
        }
        
        result = append(result, data[SEMITRIVIAL_GROUPS_INDEX]);
        result = append(result, new long[trivialGroupCount]);
        return result;
    }
}
