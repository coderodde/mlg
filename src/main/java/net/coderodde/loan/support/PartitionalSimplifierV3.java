package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import net.coderodde.loan.Utilities;

/**
 * This simplifier improves on 
 * {@link net.coderodde.loan.support.PartitionalSimplifierV2} by counting the 
 * amount of groups in the input array (<tt>N</tt>) and starting partitioning 
 * from <tt>N</tt> blocks.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class PartitionalSimplifierV3 extends Simplifier {

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
                            simplifyImpl(gs.positiveArray, 
                                         gs.negativeArray, 
                                         initialBlocks) :
                            simplifyImpl(gs.negativeArray, 
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
