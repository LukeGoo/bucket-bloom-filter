package top.geyalu.bloomfilter.core;

import org.apache.hadoop.util.hash.Hash;

/**
 * Wrap ByteBloomFilter for bucket usage
 *
 * @author Ge Yalu
 * created on 2019/6/4
 */
public class BloomFilterHolder {

    public ByteBloomFilter byteBloomFilter;
    private int bucketId;

    /**
     * @param allocByteBuffer whether to initialize ByteBuffer in ByteBloomFilter
     */
    BloomFilterHolder(int bucketId, int maxKeys, double errorRate, int foldFactor, boolean allocByteBuffer) {
        this.bucketId = bucketId;

        byteBloomFilter = new ByteBloomFilter(maxKeys, errorRate, Hash.MURMUR_HASH, foldFactor);

        if (allocByteBuffer) {
            byteBloomFilter.allocBloom();
        }
    }

    public void add(byte[] key) {
        this.byteBloomFilter.add(key);

    }

    public boolean check(byte[] key) {
        return this.byteBloomFilter.contains(key);
    }

    public int getBucketId() {
        return bucketId;
    }

    public void setBucketId(int bucketId) {
        this.bucketId = bucketId;
    }
}
