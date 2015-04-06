package net.coderodde.loan;

import java.util.Random;
import static net.coderodde.loan.Utilities.countGroups;
import static net.coderodde.loan.Utilities.createEquityArray;
import static net.coderodde.loan.Utilities.print;
import net.coderodde.loan.support.CombinationGenerator;
import net.coderodde.loan.support.CombinatorialSimplifier;
import net.coderodde.loan.support.GeneralPartitionGenerator;
import net.coderodde.loan.support.GreedyCombinatorialSimplifier;
import net.coderodde.loan.support.PartitionalSimplifierV1;
import net.coderodde.loan.support.PartitionalSimplifierV2;
import net.coderodde.loan.support.PartitionalSimplifierV3;
import net.coderodde.loan.support.PartitionalSimplifierV4;
import net.coderodde.loan.support.SpecialPartitionGenerator;

/**
 * This class demonstrates the performance of simplifiers.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class Demo {
    
    public static final String endl = "\n";
    
    /**
     * The amount of nodes in the demo graphs.
     */
    private static final int LENGTH = 50;
    
    /**
     * The minimum arc weight.
     */
    private static final long MIN_WEIGHT = -10L;
    
    /**
     * The maximum arc weight.
     */
    private static final long MAX_WEIGHT = 20L;
    
    /**
     * The entry point of a demonstration program.
     * 
     * @param args ignored.
     */
    public static void main(final String... args) {
//        CombinationGenerator cg = new CombinationGenerator(5);
//        
//        int line = 1;
//        
//        while (cg.inc()) {
//            if (line == 14) {
//                cg.remove();
//                cg.inc();
//            }
//            System.out.printf("%3d: ", line++);
//            print(cg.getIndices());
//            
//        }
//        System.exit(0);
        
        final long seed = 1428336986515L;//System.currentTimeMillis();
        final Random rnd = new Random();
        
        System.out.println("Seed:        " + seed);
        System.out.println("Graph size:  " + LENGTH);
        
        final long[] graph = createEquityArray(LENGTH,
                                               rnd,
                                               MIN_WEIGHT,
                                               MAX_WEIGHT,
                                               0.3f);
        
        System.out.println("Easy groups: " + countGroups(graph) + endl);
        System.out.print("Input: ");
        
        Utilities.print(graph);
        
        System.out.println();
        
        profile(new GreedyCombinatorialSimplifier(), graph);
        profile(new CombinatorialSimplifier(), graph);
//        profile(new PartitionalSimplifierV1(), graph);
        profile(new PartitionalSimplifierV2(), graph);
        profile(new PartitionalSimplifierV3(), graph);
        profile(new PartitionalSimplifierV4(), graph);
    }
    
    /**
     * Profiles the input graph against the input simplifier.
     * 
     * @param simplifier the simplifier to use.
     * @param array      the graph to minimize.
     */
    private static void profile(final Simplifier simplifier,
                                final long[] array) {
        System.out.println("--- " + simplifier.getClass().getName());
        
        final long ta = System.currentTimeMillis();
        final long[] result = simplifier.simplify(array);
        final long tb = System.currentTimeMillis();
        
        System.out.println("Time: " + (tb - ta) + " ms. Groups: " + 
                           countGroups(result));
        Utilities.print(result);
        System.out.println();
    }
    
    private static final long num(final long n, final long k) {
        if (n == 0L && k == 0L) {
            return 1L;
        }
        
        if (n == 0L || k == 0L) {
            return 0L;
        }
        
        return k * num(n - 1, k) + num(n - 1, k - 1);
    }
    
    private static final void shit() {
        int n = 6, k = 3;
        SpecialPartitionGenerator spg = new SpecialPartitionGenerator(n, k);
        GeneralPartitionGenerator pg = new GeneralPartitionGenerator(n, k);
        long total = 0;
        
        for (int i = k; i <= n; ++i) {
            total += num(n, i);
        }
        
        System.out.println("Total: " + total);
        
        int index = 0;
        
        do {
            System.out.printf("%2d: ", ++index);
            
            int[] ind = spg.getIndices();
            for (int i = 0; i < ind.length; ++i) {
                System.out.print(ind[i]);
            }
            
            System.out.println();
        } while (spg.inc());
    }
    
    private static final void print(final int[] arr) {
        for (final int i : arr) {
            System.out.print(i + " ");
        }
        
        System.out.println();
    }
}
