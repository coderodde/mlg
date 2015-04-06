package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import net.coderodde.loan.Utilities;

/**
 * This simplifier generates the partition in "reversed" order: at the 
 * beginning, each node of a smaller array is a block, and the generation of
 * blocks proceeds towards blocks of larger size.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class PartitionalSimplifierV4 extends Simplifier {

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
        
        final long[][] data = stripSemitrivialGroups(array1);
        final GraphSplit gs = split(data[NONTRIVIAL_GROUPS_INDEX]);
        
        long[] result = new long[0];
        
        final int initialBlocks = 
                Utilities.countGroups(data[NONTRIVIAL_GROUPS_INDEX]);
        
        if (gs.positiveArray.length > 0) {
            result = gs.positiveArray.length < gs.negativeArray.length ?
                            simplifyImplReversed(gs.positiveArray, 
                                                 gs.negativeArray, 
                                                 initialBlocks) :
                            simplifyImplReversed(gs.negativeArray, 
                                                 gs.positiveArray,
                                                 initialBlocks);
        }
        
        if (trivialGroupCount > 0) {
            final long[] ret = new long[result.length + trivialGroupCount];
            System.arraycopy(result, 0, ret, 0, result.length);
            return append(ret, data[SEMITRIVIAL_GROUPS_INDEX]);
        } else {
            return result;
        }   
    }
}
