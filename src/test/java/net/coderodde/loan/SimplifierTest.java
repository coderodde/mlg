package net.coderodde.loan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.coderodde.loan.Simplifier.GraphSplit;
import net.coderodde.loan.Simplifier.GroupSplit;
import static net.coderodde.loan.Simplifier.append;
import static net.coderodde.loan.Simplifier.mypow;
import static net.coderodde.loan.Simplifier.split;
import static net.coderodde.loan.Simplifier.splitBySign;
import org.junit.Test;
import static org.junit.Assert.*;

public class SimplifierTest {
  
    @Test 
    public void testGroupSplit() {
        final long[] graph = new long[]{ 2, 0, -3, 1, 4, -4, 0};
        final GroupSplit gs = split(graph);
        
        assertEquals(2, gs.trivialGroups.length);
        assertEquals(2, gs.semitrivialGroups.length);
        assertEquals(3, gs.nontrivialGroups.length);
        
        assertEquals(0, gs.trivialGroups[0]);
        assertEquals(0, gs.trivialGroups[1]);
        
        Arrays.sort(gs.semitrivialGroups);
        
        assertEquals(-4, gs.semitrivialGroups[0]);
        assertEquals(4, gs.semitrivialGroups[1]);
        
        Arrays.sort(gs.nontrivialGroups);
        
        assertEquals(-3, gs.nontrivialGroups[0]);
        assertEquals(1, gs.nontrivialGroups[1]);
        assertEquals(2, gs.nontrivialGroups[2]);
    }
    
    @Test
    public void testMypow() {
        assertEquals(1L, mypow(2L, 0L));
        assertEquals(1L, mypow(5L, 0L));
        assertEquals(3L, mypow(3L, 1L));
        assertEquals(9L, mypow(3L, 2L));
        assertEquals(27L, mypow(3L, 3L));
        assertEquals(16L, mypow(2L, 4L));
    }
    
    @Test
    public void testSplitBySign() {
        final long[] graph = new long[]{2L, -3L, 5L, -9L, 5L};
        final GraphSplit gs = splitBySign(graph);
        
        assertEquals(2L, gs.positiveArray[0]);
        assertEquals(5L, gs.positiveArray[1]);
        assertEquals(5L, gs.positiveArray[2]);
        
        assertEquals(-3L, gs.negativeArray[0]);
        assertEquals(-9L, gs.negativeArray[1]);
    }
    
    @Test
    public void testListSplit() {
        final List<Long> list = new ArrayList<>();
        
        for (int i = 0; i < 10; ++i) {
            list.add((long) i);
        }
        
        final boolean[] flags = new boolean[list.size()];
        flags[1] = flags[3] = flags[7] = true;
        
        final List<Long>[] result = split(list, flags);
        
        assertEquals(1, (long) result[0].get(0));
        assertEquals(3, (long) result[0].get(1));
        assertEquals(7, (long) result[0].get(2));
        
        assertEquals(0, (long) result[1].get(0));
        assertEquals(2, (long) result[1].get(1));
        assertEquals(4, (long) result[1].get(2));
        assertEquals(5, (long) result[1].get(3));
        assertEquals(6, (long) result[1].get(4));
        assertEquals(8, (long) result[1].get(5));
        assertEquals(9, (long) result[1].get(6));
    }
    
    @Test
    public void testAppend() {
        final long[] arr1 = new long[]{1, 0, -3};
        final long[] arr2 = new long[]{0, 4, -6};
        final long[] ret = append(arr1, arr2);
        
        assertEquals(1,  ret[0]);
        assertEquals(0,  ret[1]);
        assertEquals(-3, ret[2]);
        assertEquals(0,  ret[3]);
        assertEquals(4,  ret[4]);
        assertEquals(-6, ret[5]);
    }
}
