package net.coderodde.loan.support;

public class GeneralPartitionGenerator {

    /**
     * The total amount of elements in a set.
     */
    private final int n;
    
    /** 
     * The amount of blocks in the current partition.
     */
    private int k;
    
    /**
     * The partition indices. The element with index <code>i</code> should be
     * put in the block <code>indices[i]</code>.
     */
    private int[] indices;
    
    /**
     * The actual generator for current <tt>k</tt>.
     */
    private SpecialPartitionGenerator generator;
    
    /**
     * Constructs a new partition generator generating all possible partitions.
     * 
     * @param n the amount of elements being partitioned.
     */
    public GeneralPartitionGenerator(final int n) {
        this(n, 1);
    }
    
    /**
     * Constructs a new partition generator generating all partitions with at
     * least <code>startingBlocks</code> blocks.
     * 
     * @param n              the size of the set to partition.
     * @param startingBlocks the minimum amount of blocks in the partition.
     */
    public GeneralPartitionGenerator(final int n, final int startingBlocks) {
        check(n);
        this.n = n;
        this.k = startingBlocks;
        this.generator = new SpecialPartitionGenerator(n, k);
        this.indices = generator.getIndices();
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
        
        if (k < n) {
            generator = new SpecialPartitionGenerator(n, ++k);
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

    private void check(final int n) {
        if (n < 1) {
            throw new IllegalArgumentException("'n' < 1.");
        }
    }
}
