package net.coderodde.loan.support;

import net.coderodde.loan.Simplifier;

/**
 * This simplifier implements a simplification algorithm, which splits the input
 * graph into two lists: a negative and a positive list. It then chooses the
 * smaller of them and splits it in all possible partitions. For each partition
 * of the latter list, the algorithm generates all possible 
 * <tt>k</tt>-partitions of the larger set, where <tt>k</tt> is the amount of 
 * blocks in the partition of the smaller set. 
 * 
 * @author Rodion Efremov
 * @version  1.6
 */
public class PartitionalSimplifier extends Simplifier {

    @Override
    public long[] simplify(long[] graph) {
        final long[] array1 = stripTrivialGroups(graph);
        final int trivialGroupCount = graph.length - array1.length;
        final GraphSplit gs = split(array1);
        
        long[] result;
        
        result = gs.positiveArray.length < gs.negativeArray.length ?
                 simplifyImplPositiveOutermost(gs) :
                 simplifyImplNegativeOutermost(gs);
        
        if (trivialGroupCount > 0) {
            final long[] ret = new long[graph.length];
            System.arraycopy(result, 0, ret, 0, result.length);
            return ret;
        } else {
            return result;
        }
    }
    
    private long[] simplifyImplPositiveOutermost(final GraphSplit gs) {
        final long[] positiveArray = gs.positiveArray;
        final long[] negativeArray = gs.negativeArray;
        
        final PartitionGenerator positiveGenerator =
                new PartitionGenerator(positiveArray.length);
        
        final int[] bestPositiveIndices = new int[positiveArray.length];
        final int[] bestNegativeIndices = new int[negativeArray.length];
        final int[] positiveIndices = positiveGenerator.getIndices();
        
        int bestGroupAmount = 0;
        int bestk = -1;
        
        do {
            final int k = positiveGenerator.getk();
            
            final SpecialPartitionGenerator negativeGenerator = 
                    new SpecialPartitionGenerator(negativeArray.length, k);
            
            final int[] negativeIndices = negativeGenerator.getIndices();
            
            do {
                int groups = countGroups(positiveArray,
                                         negativeArray,
                                         positiveIndices,
                                         negativeIndices,
                                         k);
                
                if (bestGroupAmount < groups) {
                    bestGroupAmount = groups;
                    bestk = k;
                    
                    System.arraycopy(positiveIndices, 
                                     0, 
                                     bestPositiveIndices,
                                     0,
                                     positiveIndices.length);
                    
                    System.arraycopy(negativeIndices,
                                     0, 
                                     bestNegativeIndices, 
                                     0, 
                                     negativeIndices.length);
                }
            } while (negativeGenerator.inc());
        } while (positiveGenerator.inc());
        
        return buildSolution(positiveArray,
                             negativeArray,
                             bestPositiveIndices,
                             bestNegativeIndices,
                             bestk);
    }
    
    private long[] simplifyImplNegativeOutermost(final GraphSplit gs) {
        final long[] positiveArray = gs.positiveArray;
        final long[] negativeArray = gs.negativeArray;
        
        final PartitionGenerator negativeGenerator =
                new PartitionGenerator(negativeArray.length);
        
        final int[] bestPositiveIndices = new int[positiveArray.length];
        final int[] bestNegativeIndices = new int[negativeArray.length];
        final int[] negativeIndices = negativeGenerator.getIndices();
        
        int bestGroupAmount = 0;
        int bestk = -1;
        
        do {
            final int k = negativeGenerator.getk();
            
            final SpecialPartitionGenerator positiveGenerator = 
                    new SpecialPartitionGenerator(positiveArray.length, k);
            
            final int[] positiveIndices = positiveGenerator.getIndices();
            
            do {
                int groups = countGroups(positiveArray,
                                         negativeArray,
                                         positiveIndices,
                                         negativeIndices,
                                         k);
                
                if (bestGroupAmount < groups) {
                    bestGroupAmount = groups;
                    bestk = k;
                    
                    System.arraycopy(positiveIndices, 
                                     0, 
                                     bestPositiveIndices,
                                     0,
                                     positiveIndices.length);
                    
                    System.arraycopy(negativeIndices,
                                     0, 
                                     bestNegativeIndices, 
                                     0, 
                                     negativeIndices.length);
                }
            } while (positiveGenerator.inc());
        } while (negativeGenerator.inc());
        
        return buildSolution(positiveArray,
                             negativeArray,
                             bestPositiveIndices,
                             bestNegativeIndices,
                             bestk);
    }
    
    
}
