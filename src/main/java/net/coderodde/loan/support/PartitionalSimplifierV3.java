package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import net.coderodde.loan.Utilities;
import static net.coderodde.loan.Utilities.checkIsGroup;

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
        checkIsGroup(graph);
        
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final GroupSplit gs = split(graph);
        
        if (gs.trivialGroups.length == graph.length) {
            return graph.clone();
        }
        
        if (gs.nontrivialGroups.length == 0) {
            return append(gs.trivialGroups, gs.semitrivialGroups);
        }
        
        final GraphSplit gs2 = splitBySign(gs.nontrivialGroups);
        final int initialBlocks = 
                Utilities.countGroups(gs.nontrivialGroups);
        
        long[] result = gs2.positiveArray.length < gs2.negativeArray.length ?
                            simplifyImpl(gs2.positiveArray, 
                                         gs2.negativeArray,
                                         initialBlocks) :
                            simplifyImpl(gs2.negativeArray, 
                                         gs2.positiveArray,
                                         initialBlocks);
        
        result = append(result, gs.trivialGroups);
        result = append(result, gs.semitrivialGroups);
        return result;
    }
}
