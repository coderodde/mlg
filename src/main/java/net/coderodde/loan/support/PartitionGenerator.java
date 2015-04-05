package net.coderodde.loan.support;

public class PartitionGenerator {

    /**
     * The total amount of elements in a set.
     */
    private final int n;

    /**
     * The indices for a partition.
     */
    private final int[] s;
    
    /**
     * Internal book-keeping.
     */
    private final int[] m;

    /**
     * Constructs a new partition generator generating all possible partitions.
     * 
     * @param n the amount of elements being partitioned.
     */
    public PartitionGenerator(final int n) {
        check(n);
        this.n = n;
        this.s = new int[n];
        this.m = new int[n];
    }

    /**
     * Increments to the next partition, returning <code>true</code> if there is
     * the next permutation. If there is no next permutation, <code>false</code>
     * is returned.
     * 
     * @return <code>true</code> if the increment was successful, 
     *         <code>false</code> otherwise.
     */
    public boolean inc() {
        int i = 0;
        ++s[i];
        
        while (i < n - 1 && s[i] > m[i] + 1) {
            s[i++] = 0;
            ++s[i];
        }
        
        if (i == n - 1) {
            return false;
        }
        
        int max = s[i];
        
        for (--i; i >= 0; --i) {
            m[i] = max;
        }
        
        return true;
    }
    
    /**
     * Returns the indices for a partition.
     * 
     * @return the indices for a partition.
     */
    public int[] getIndices() {
        return s;
    }
    
    /**
     * Returns the amount of blocks in the current partition.
     * 
     * @return the amount of blocks.
     */
    public int getk() {
        int i = 0;
        
        for (int j : s) {
            if (i < j) {
                i = j;
            }
        }
        
        return i + 1;
    }

    private void check(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("'n' < 1.");
        }
    }
}
