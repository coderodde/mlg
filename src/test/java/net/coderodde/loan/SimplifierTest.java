package net.coderodde.loan;

import java.util.Arrays;
import net.coderodde.loan.Simplifier.GraphSplit;
import static net.coderodde.loan.Simplifier.NONTRIVIAL_GROUPS_INDEX;
import static net.coderodde.loan.Simplifier.SEMITRIVIAL_GROUPS_INDEX;
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
    
    @Test
    public void testStripSemitrivialNodesGroups() {
        final long[] graph = new long[]{2L, 3L, -2L, 4L, -3L, 4L, -3L, -4L, 6L};
        final long[][] ret = Simplifier.stripSemitrivialGroups(graph);
        final long[] semitrivialArray = ret[SEMITRIVIAL_GROUPS_INDEX];
        final long[] nontrivialArray = ret[NONTRIVIAL_GROUPS_INDEX];
        
        assertEquals(6, semitrivialArray.length);
        assertEquals(3, nontrivialArray.length);
        
        assertEquals(2L, semitrivialArray[0]);
        assertEquals(-2L, semitrivialArray[1]);
        assertEquals(3L, semitrivialArray[2]);
        assertEquals(-3L, semitrivialArray[3]);
        assertEquals(4L, semitrivialArray[4]);
        assertEquals(-4L, semitrivialArray[5]);
        
        Arrays.sort(nontrivialArray);
        
        assertEquals(-3L, nontrivialArray[0]);
        assertEquals(4L, nontrivialArray[1]);
        assertEquals(6L, nontrivialArray[2]);
    }
}
