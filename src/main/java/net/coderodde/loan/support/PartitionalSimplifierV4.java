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
        
        result = append(result, data[SEMITRIVIAL_GROUPS_INDEX]);
        result = append(result, new long[trivialGroupCount]);
        return result;
    }
}
