package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import net.coderodde.loan.Utilities;
import static net.coderodde.loan.Utilities.checkIsGroup;

/**
 * This simplifier generates the partition in "reversed" order: at the 
 * beginning, each node of a smaller array is a block, and the generation of
 * blocks proceeds towards blocks of larger size.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class PartitionalSimplifierV4 extends Simplifier {

    /**
     * Implements a partitional approach for founding the groups: the algorithm
     * splits the graph into positive and negative arrays by equities, choses
     * the smaller one, and splits it in maximal amount of blocks (one node per
     * block). Then the algorithm proceeds towards larger blocks, and as soon
     * it has a group match, the optimal solution is found.
     * 
     * @param  graph the graph to simplify.
     * @return a simplified graph.
     */
    @Override
    public long[] simplify(long[] graph) {
        checkIsGroup(graph);
       
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final GroupSplit gs = split2(graph);
        
        if (gs.trivialGroups.length == graph.length) {
            return graph.clone();
        }
        
        if (gs.nontrivialGroups.length == 0) {
            return append(gs.trivialGroups, gs.semitrivialGroups);
        }
        
        final GraphSplit gs2 = split(gs.nontrivialGroups);
        final int initialBlocks = 
                Utilities.countGroups(gs.nontrivialGroups);
        
        long[] result = gs2.positiveArray.length < gs2.negativeArray.length ?
                            simplifyImplReversed(gs2.positiveArray, 
                                                 gs2.negativeArray,
                                                 initialBlocks) :
                            simplifyImplReversed(gs2.negativeArray, 
                                                 gs2.positiveArray,
                                                 initialBlocks);
        
        result = append(result, gs.trivialGroups);
        result = append(result, gs.semitrivialGroups);
        return result;
    }
}
