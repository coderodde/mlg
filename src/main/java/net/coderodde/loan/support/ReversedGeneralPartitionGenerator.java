package net.coderodde.loan.support;

/**
 * The partition generator generates partitions in reverse order (by amount of
 * blocks in the partition). 
 * 
 * @author Rodion Efremov
 * @version 1.6
 */
public class ReversedGeneralPartitionGenerator {

    /**
     * The total amount of elements in a set.
     */
    private final int n;
    
    /** 
     * The amount of blocks in the current partition.
     */
    private int k;
    
    /**
     * The minimum block amount for partitions.
     */
    private final int minimumBlocks;
    
    /**
     * The actual generator for current <tt>k</tt>.
     */
    private SpecialPartitionGenerator generator;
    
    /**
     * Constructs a new partition generator generating all possible partitions.
     * 
     * @param n the amount of elements being partitioned.
     */
    public ReversedGeneralPartitionGenerator(final int n) {
        this(n, 1);
    }
    
    /**
     * Constructs a new partition generator generating all partitions with at
     * least <code>startingBlocks</code> blocks.
     * 
     * @param n             the size of the set to partition.
     * @param minimumBlocks the minimum amount of blocks in the partition.
     */
    public ReversedGeneralPartitionGenerator(final int n, 
                                             final int minimumBlocks) {
        this.minimumBlocks = minimumBlocks;
        this.k = n;
        this.n = n;
        this.generator = new SpecialPartitionGenerator(n, n);
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
        if (generator.inc()) {
            return true;
        }
        
        if (k > minimumBlocks) {
            generator = new SpecialPartitionGenerator(n, --k);
            return true;
        }
        
        return false;
    }
    
    /**
     * Returns the indices for a partition.
     * 
     * @return the indices for a partition.
     */
    public int[] getIndices() {
        return generator.getIndices();
    }
    
    /**
     * Returns the amount of blocks in the current partition.
     * 
     * @return the amount of blocks.
     */
    public int getk() {
        return k;
    }

    /**
     * Checks that the set being partitioned is not empty.
     * 
     * @param n the size of the set being partitioned.
     */
    private void check(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("'n' < 1.");
        }
    }    
}
