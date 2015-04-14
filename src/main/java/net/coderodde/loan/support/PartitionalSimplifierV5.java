package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;
import net.coderodde.loan.Utilities;
import static net.coderodde.loan.Utilities.checkIsGroup;

/**
 * This simplifier relies on two threads partitioning the the list in order to
 * find the proper groups. One thread proceeds from least blocks, incrementing
 * their amount, and the other starting from the largest possible amount of 
 * blocks, and proceeding towards a smaller amount of blocks. Such an 
 * arrangement guarantees that (if at least a dual-core CPU is available) this
 * simplifier will not take any more time than the least time needed by
 * {@link net.coderodde.loan.support.PartitionalSimplifierV3} and 
 * {@link net.coderodde.loan.support.PartitionalSimplifierV4}.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class PartitionalSimplifierV5 extends Simplifier {

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

        long[] result;
        
        if (Runtime.getRuntime().availableProcessors() < 2) {
            result = gs2.positiveArray.length < gs2.negativeArray.length ?
                                simplifyImplReversed(gs2.positiveArray, 
                                                     gs2.negativeArray,
                                                     initialBlocks) :
                                simplifyImplReversed(gs2.negativeArray, 
                                                     gs2.positiveArray,
                                                     initialBlocks);
            
        } else {
            result = gs2.positiveArray.length < gs2.negativeArray.length ?
                    simplifyByPartitioningUsingThreads(gs2.positiveArray,
                                                       gs2.negativeArray,
                                                       initialBlocks) :
                    simplifyByPartitioningUsingThreads(gs2.negativeArray,
                                                       gs2.positiveArray,
                                                       initialBlocks);
        }
        
        result = append(result, gs.trivialGroups);
        result = append(result, gs.semitrivialGroups);
        return result;
    }    
}
