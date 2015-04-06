package net.coderodde.loan.support;

/**
 * This class generates all possible partition of a set into exactly <tt>k</tt> 
 * blocks.
 *
 * @author Rodion Efremov
 * @version 1.6
 */
public class SpecialPartitionGenerator {

    /**
     * The total amount of elements in a set.
     */
    private final int n;
    
    /**
     * The total amount of blocks.
     */
    private final int k;
    
    /**
     * The indices for a partition.
     */
    private final int[] s;
    
    /**
     * Internal book-keeping.
     */
    private final int[] m;

    /**
     * Constructs this permutation generator.
     * 
     * @param n the size of the list to partition.
     * @param k the amount of blocks in the partition.
     */
    public SpecialPartitionGenerator(final int n, final int k) {
        check(n, k);
        this.n = n;
        this.k = k;
        this.s = new int[n];
        this.m = new int[n];
        
        for (int i = 0; i < n - k + 1; ++i) {
            s[i] = m[i] = 0;
        }
        
        for (int i = n - k + 1; i < n; ++i) {
            s[i] = m[i] = i - n + k;
        }
    }

    /**
     * Tries to increment to the next partition. If incremental was successful,
     * <code>true</code> is returned. Otherwise, <code>false</code> is returned.
     * @return 
     */
    public boolean inc() {
        for (int i = n - 1; i > 0; --i) {
            if (s[i] < k - 1 && s[i] <= m[i - 1]) {
                s[i]++;
                m[i] = Math.max(m[i], s[i]);
                
                for (int j = i + 1; j < n - k + m[i] + 1; ++j) {
                    s[j] = 0;
                    m[j] = m[i];
                }
                
                for (int j = n - k + m[i] + 1; j < n; ++j) {
                    s[j] = m[j] = k - n + j;
                }
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Returns the array of indices.
     * 
     * @return indices.
     */
    public int[] getIndices() {
        return s;
    }
    
    private void check(final int n, final int k) {
        if (n < 1) {
            throw new IllegalArgumentException("'n' < 1.");
        }
        if (k < 1) {
            throw new IllegalArgumentException("'k' < 1.");
        }
        if (k > n) {
            throw new IllegalArgumentException("'k' > 'n'.");
        }
    }
}
