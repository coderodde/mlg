package net.coderodde.loan.support;

/**
 * This class generates indices for list combinations.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class CombinationGenerator {

    /**
     * The minimum size of a set to index.
     */
    private static final int MINIMUM_SIZE = 1;

    /**
     * Denotes the upper bound on amount of indices and the maximum value of
     * each index.
     */
    private int n;
    
    /**
     * Denotes the current length of index sets.
     */
    private int k;
    
    /**
     * Stores the actual indices.
     */
    private int[] indices;

    /**
     * Creates a new combination generator.
     * 
     * @param n the size of a list for which to create combinations.
     */
    public CombinationGenerator(final int n) {
        checkSize(n);
        this.n = n;
        this.k = 1;
        this.indices = new int[1];
        this.indices[0] = -1;
    }

    /**
     * Attempts to generate the next combination.
     *     
     * @return <code>true</code> if the next combination was successfully 
     *         generated, <code>false</code> if there is no more combinations.
     */
    public boolean inc() {
        if (k < 1 || k > n) {
            return false;
        }
        
        if (indices[k - 1] == n - 1) {
            int i;
            
            for (i = k - 2; i >= 0; i--) {
                if (indices[i] + 1 < indices[i + 1]) {
                    ++indices[i++];
                     
                    while (i < k) {
                        indices[i] = indices[i - 1] + 1;
                        ++i;
                    }
                    
                    return true;
                }
            }
            
            k++;
            
            if (k > n) {
                return false;
            }
            
            indices = new int[k];
            
            for (int j = 0; j < k; ++j) {
                indices[j] = j;
            }
        } else {
            ++indices[k - 1];
        }
        
        return true;
    }

    /**
     * Returns the combination indices.
     * 
     * @return the indices for the current combination.
     */
    public int[] getIndices() {
        return indices;
    }
    
    /**
     * Returns the size of the current combination.
     * 
     * @return the size of the current combination.
     */
    public int getCombinationSize() {
        return k;
    }

    /**
     * Returns <code>true</code> if there is no gaps between two consecutive 
     * indices.
     * 
     * @return <code>true</code> if the indices have no gaps.
     */
    public boolean hasNoGaps() {
        for (int i = 0; i < k - 1; ++i) {
            if (indices[i] + 1 != indices[i + 1]) {
                return false;
            }
        }
        
        return true;
    }

    /**
     * Resets this combination generator.
     */
    public void reset() {
        k = 1;
        indices = new int[]{-1};
    }

    /**
     * "Removes" the current combination.
     */
    public void remove() {
        final int oldn = n;
        
        n -= k;
        
        if (n == 0) {
            return;
        }
        
        if (k > n) {
            k = n;
            indices = new int[k];
            
            for (int i = 0; i < k; ++i) {
                indices[i] = i;
            }
        } else if (n == k) {
            for (int i = 0; i < k; ++i) {
                indices[i] = i;
            }
        } else {
            final int emptyRightSpots = oldn - k - indices[0];
            
            if (emptyRightSpots < k) {
                indices = new int[++k];
            
                for (int i = 1; i < k; ++i) {
                    indices[i] = i;
                }
            } else {
                for (int i = 1, j = indices[0] + 1; i < k; ++i, ++j) {
                    indices[i] = j;
                }
            }
        }
        
        --indices[k - 1];
    }

    /**
     * Checks that the set size is not too small.
     * 
     * @param n the size of a set for which to generate the combinations.
     */
    private void checkSize(final int n) {
        if (n < MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                    "'n' must be at least " + MINIMUM_SIZE);
        }
    }
}
