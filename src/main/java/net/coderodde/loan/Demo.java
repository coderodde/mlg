package net.coderodde.loan;

import java.util.Random;
import static net.coderodde.loan.Utilities.countGroups;
import static net.coderodde.loan.Utilities.createEquityArray;
import net.coderodde.loan.support.CombinatorialSimplifier;
import net.coderodde.loan.support.GreedyCombinatorialSimplifier;
import net.coderodde.loan.support.PartitionalSimplifierV1;
import net.coderodde.loan.support.PartitionalSimplifierV2;
import net.coderodde.loan.support.PartitionalSimplifierV3;
import net.coderodde.loan.support.PartitionalSimplifierV4;

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
    private static final int LENGTH = 16;
    
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
        final long seed = System.currentTimeMillis();
        final Random rnd = new Random(seed);
        
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
        profile(new PartitionalSimplifierV1(), graph);
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
}
