package net.coderodde.loan.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.coderodde.loan.Simplifier;

/**
 * This simplifier starts generating small combinations of nodes and whenever a
 * combination under consideration is a group removes it from the data.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class GreedyCombinatorialSimplifier extends Simplifier {

    @Override
    public long[] simplify(long[] graph) {
        if (graph.length == 0) {
            return graph.clone();
        }
        
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        
        // If the graph consists of only trivial groups, return.
        if (trivialGroupCount == graph.length) {
            return graph.clone();
        }
        
        // Remove semi-trivial groups.
        final long[][] data1 = stripSemitrivialGroups(array1);
        final GraphSplit gs = split(data1[NONTRIVIAL_GROUPS_INDEX]);
        
        final List<Long> positiveList = 
                new ArrayList<>(gs.positiveArray.length);
        
        final List<Long> negativeList =
                new ArrayList<>(gs.negativeArray.length);
        
        for (final long l : gs.positiveArray) {
            positiveList.add(l);
        }
        
        for (final long l : gs.negativeArray) {
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
        
        outer:
        while (positiveGenerator.inc()) {
            positiveIndices = positiveGenerator.getIndices();
            
            final long currentPositiveSum = sum(positiveList, positiveIndices);
            
            final CombinationGenerator negativeGenerator =
                    new CombinationGenerator((negativeList.size()));
            
            while (negativeGenerator.inc()) {
                negativeIndices = negativeGenerator.getIndices();
                
                final long currentNegativeSum = sum(negativeList, 
                                                    negativeIndices);
                
                if (currentNegativeSum > currentPositiveSum) {
                    if (negativeGenerator.hasNoGaps()) {
                        continue outer;
                    }
                } else if (currentPositiveSum == currentNegativeSum) {
                    final List<Long> group = new ArrayList<>();
                    
                    for (final int index : positiveIndices) {
                        group.add(positiveList.get(index));
                    }
                    
                    for (final int index : negativeIndices) {
                        group.add(-negativeList.get(index));
                    }
                    
                    groupList.add(group);
                    pruneList(positiveList, positiveIndices);
                    pruneList(negativeList, negativeIndices);
                    positiveGenerator.remove();
                    continue outer;
                }
            }
        }
        
        int index = 0;
        
        final long[] result = 
                new long[graph.length - trivialGroupCount 
                                      - data1[SEMITRIVIAL_GROUPS_INDEX].length];
        
        for (final List<Long> group : groupList) {
            for (final long l : group) {
                result[index++] = l;
            }
        }
        
        if (trivialGroupCount > 0) {
            final long[] ret = new long[result.length + trivialGroupCount];
            System.arraycopy(result, 0, ret, 0, result.length);
            // Append back the semi-trivial groups.
            return append(ret, data1[SEMITRIVIAL_GROUPS_INDEX]);
        } else {
            return result;
        }
    }
    
    private void pruneList(final List<Long> list, final int[] indices) {
        final int[] copy = indices.clone();
        Arrays.sort(copy);
        
        for (int i = copy.length - 1; i >= 0; --i) {
            list.remove(copy[i]);
        }
    }
}
