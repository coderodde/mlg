package net.coderodde.loan.support;

/**
 * This class generates indices for list combinations.
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class CombinationGenerator {

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
     * Returns next index list or <code>null</code> if all possible combination
     * indices were generated.
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
                --k;
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

    public int[] getIndices() {
        return indices;
    }
    
    public int getk() {
        return k;
    }

    public boolean hasNoGaps() {
        for (int i = 0; i < k - 1; ++i) {
            if (indices[i] + 1 != indices[i + 1]) {
                return false;
            }
        }
        
        return true;
    }

    public void reset() {
        k = 1;
        indices = new int[]{-1};
    }

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
            
            --indices[k - 1];
        } else if (n == k) {
            if (indices[0] == 0) {
                for (int i = 0; i < k - 1; ++i) {
                    indices[i] = i;
                }
                
                indices[k - 1] = k - 2;
            } else {
                for (int i = 0; i < k; ++i) {
                    indices[i] = i;
                }
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
            
            --indices[k - 1];
        }
    }

    private void checkSize(final int n) {
        if (n < MINIMUM_SIZE) {
            throw new IllegalArgumentException(
                    "'n' must be at least " + MINIMUM_SIZE);
        }
    }
}
