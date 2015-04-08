package net.coderodde.loan.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.coderodde.loan.Simplifier;
import static net.coderodde.loan.Utilities.checkIsGroup;

/**
 * This simplifier starts generating small combinations of nodes and whenever a
 * combination under consideration is a group removes it from the data.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class GreedyCombinatorialSimplifier extends Simplifier {

    /**
     * This algorithm generates combinations of positive nodes, and for each
     * positive combination, it generate some negative combinations.
     * <p>
     * This algorithm may be much efficient than any other simplifier in this
     * package, but this comes at expense of optimality: this simplifier may
     * returned (slightly) suboptimal solutions.
     *  
     * @param  graph the graph to simplify.
     * @return the simplified graph.
     */
    @Override
    public long[] simplify(long[] graph) {
        checkIsGroup(graph);
        
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final GroupSplit gs = split2(graph);
        
        // If the graph consists of only trivial groups, return.
        if (gs.trivialGroups.length == graph.length) {
            return graph.clone();
        }
        
        if (gs.nontrivialGroups.length == 0) {
            return append(gs.trivialGroups, gs.semitrivialGroups);
        }
        
        // Split the nontrivial group nodes in positive and negative.
        final GraphSplit graphSplit = split(gs.nontrivialGroups);
        
        final List<Long> positiveList = 
                new ArrayList<>(graphSplit.positiveArray.length);
        
        final List<Long> negativeList =
                new ArrayList<>(graphSplit.negativeArray.length);
        
        for (final long l : graphSplit.positiveArray) {
            positiveList.add(l);
        }
        
        for (final long l : graphSplit.negativeArray) {
            //// Put the absolute values instead!
            negativeList.add(-l);
        }
        
        Collections.sort(positiveList);
        Collections.sort(negativeList);
        
        final CombinationGenerator positiveGenerator =
                new CombinationGenerator(positiveList.size());
        
        int[] positiveIndices;
        int[] negativeIndices;
        
        final List<List<Long>> groupList = new ArrayList<>(graph.length);
        
        // For each positive combination, do:
        outer:
        while (positiveGenerator.inc()) {
            positiveIndices = positiveGenerator.getIndices();
            
            final long currentPositiveSum = sum(positiveList, positiveIndices);
            
            final CombinationGenerator negativeGenerator =
                    new CombinationGenerator((negativeList.size()));
            
            // For "each" negative combination, do:
            while (negativeGenerator.inc()) {
                negativeIndices = negativeGenerator.getIndices();
                
                final long currentNegativeSum = sum(negativeList, 
                                                    negativeIndices);
                
                if (currentNegativeSum > currentPositiveSum) {
                    if (negativeGenerator.hasNoGaps()) {
                        // Once here, any successing negative combination will
                        // be greater in absolute value than the current
                        // positive combination. Therefore stop generating 
                        // negative combinations and generate a new positive
                        // combination.
                        continue outer;
                    }
                } else if (currentPositiveSum == currentNegativeSum) {
                    // We have found a group.
                    final List<Long> group = new ArrayList<>();
                    
                    for (final int index : positiveIndices) {
                        group.add(positiveList.get(index));
                    }
                    
                    for (final int index : negativeIndices) {
                        // Note the minus sign. The absolute value was taken
                        // from each negative equity.
                        group.add(-negativeList.get(index));
                    }
                    
                    groupList.add(group);
                    removeFromList(positiveList, positiveIndices);
                    removeFromList(negativeList, negativeIndices);
                    positiveGenerator.remove();
                    continue outer;
                }
            }
        }
        
        int index = 0;
        
        long[] result = new long[gs.nontrivialGroups.length];
        
        // Build the solution array.
        for (final List<Long> group : groupList) {
            for (final long l : group) {
                result[index++] = l;
            }
        }
        
        result = append(result, gs.trivialGroups);
        result = append(result, gs.semitrivialGroups);
        return result;
    }
    
    /**
     * Removes from <code>list</code> all elements with indices in 
     * <code>indices</code>.
     * 
     * @param list    the list from which to remove elements.
     * @param indices the indices of elements to remove.
     */
    private void removeFromList(final List<Long> list, final int[] indices) {
        final int[] copy = indices.clone();
        Arrays.sort(copy);
        
        for (int i = copy.length - 1; i >= 0; --i) {
            list.remove(copy[i]);
        }
    }
}
