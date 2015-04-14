package net.coderodde.loan.support;

import java.util.Arrays;
import org.junit.Test;
import static org.junit.Assert.*;

public class CombinationGeneratorTest {
    
    @Test
    public void testAll() {
        CombinationGenerator g = new CombinationGenerator(5);
        
        getUntil(g, new int[]{0, 1});
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 2}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 3}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 4}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{1, 2}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{1, 3}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{1, 4}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{2, 3}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{2, 4}));
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{3, 4}));
        assertEquals(2, g.getCombinationSize());
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 1, 2}));
        assertEquals(3, g.getCombinationSize());
        
        g.reset();
        assertEquals(1, g.getCombinationSize());
        
        getUntil(g, new int[]{1, 2, 4});
        g.remove();
        g.inc();
        
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 1}));
        
        g = new CombinationGenerator(5);
        getUntil(g, new int[]{2});
        g.remove();
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{2}));
        
        g = new CombinationGenerator(5);
        getUntil(g, new int[]{2, 4});
        g.remove();
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 1, 2}));
        
        g = new CombinationGenerator(5);
        getUntil(g, new int[]{0, 3});
        g.remove();
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 1}));
        
        g = new CombinationGenerator(5);
        getUntil(g, new int[]{2, 4});
        g.remove();
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{0, 1, 2}));
        
        g = new CombinationGenerator(10);
        getUntil(g, new int[]{4, 7});
        g.remove();
        
        assertTrue(g.inc());
        assertTrue(Arrays.equals(g.getIndices(), new int[]{4, 5}));
    }
    
    private static void getUntil(final CombinationGenerator g, 
                                 final int... indices) {
        g.reset();
        
        while (!Arrays.equals(g.getIndices(), indices)) {
            g.inc();
        }
    }
}
