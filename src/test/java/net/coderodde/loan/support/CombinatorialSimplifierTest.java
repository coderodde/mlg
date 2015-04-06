package net.coderodde.loan.support;

import static net.coderodde.loan.support.CombinatorialSimplifier.mypow;
import org.junit.Test;
import static org.junit.Assert.*;

public class CombinatorialSimplifierTest {

    @Test
    public void testMypow() {
        assertEquals(1L, mypow(2L, 0L));
        assertEquals(1L, mypow(5L, 0L));
        assertEquals(3L, mypow(3L, 1L));
        assertEquals(9L, mypow(3L, 2L));
        assertEquals(27L, mypow(3L, 3L));
        assertEquals(16L, mypow(2L, 4L));
    }
    
}
