package net.coderodde.loan;

import net.coderodde.loan.Simplifier.GraphSplit;
import static net.coderodde.loan.Simplifier.split;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimplifierTest {
    
    @Test
    public void testStripTrivialGroups() {
        final long[] graph = new long[]{0, 1L, 0L, 0L, 4L, -5L, 0L};
        final long[] strip = Simplifier.stripTrivialGroups(graph);
        assertEquals(3, strip.length);
        assertEquals(1L, strip[0]);
        assertEquals(4L, strip[1]);
        assertEquals(-5L, strip[2]);
    }
    
    @Test
    public void testSplit() {
        final long[] graph = new long[]{2L, -3L, 5L, -9L, 5L};
        final GraphSplit gs = split(graph);
        
        assertEquals(2L, gs.positiveArray[0]);
        assertEquals(5L, gs.positiveArray[1]);
        assertEquals(5L, gs.positiveArray[2]);
        
        assertEquals(-3L, gs.negativeArray[0]);
        assertEquals(-9L, gs.negativeArray[1]);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testSplitThrowsOnTrivialGroup() {
        final long[] graph = new long[]{2L, -3L, 5L, -9L, 5L, 0L};
        split(graph);
    }
}
