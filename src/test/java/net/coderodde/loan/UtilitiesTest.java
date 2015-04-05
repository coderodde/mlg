package net.coderodde.loan;

import static net.coderodde.loan.Utilities.countGroups;
import static net.coderodde.loan.Utilities.isGroup;
import org.junit.Test;
import static org.junit.Assert.*;

public class UtilitiesTest {
    
    @Test
    public void testIsGroup() {
        assertTrue(isGroup(new long[]{ 0L, -3L, 2L, -1L, 2L}));
        assertFalse(isGroup(new long[]{ 0L, -2L, 2L, -1L, 2L}));
        assertFalse(isGroup(new long[]{ 0L, -4L, 2L, -1L, 2L}));
    }

    @Test
    public void testCountGroups() {
        assertEquals(3, countGroups(new long[]{ -1L, 3L, -2L, 0, 10L, -4L, -7L, 1L}));
    }
    
    @Test(expected = IllegalArgumentException.class) 
    public void testCountGroupsThrowsOnNonGroup() {
        countGroups(new long[]{ -1L, 3L, -2L, 0, 10L, -4L, 7L});
    }
}
