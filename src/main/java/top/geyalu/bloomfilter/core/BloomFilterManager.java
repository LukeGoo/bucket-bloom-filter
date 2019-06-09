package top.geyalu.bloomfilter.core;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * this class hold all buckets, control buckets and provide load,dump,check,add function
 *
 * @author Ge Yalu
 * created on 2019/6/4
 */

public class BloomFilterManager {
    private static final Logger logger = LoggerFactory.getLogger(BloomFilterManager.class);

    private static final String DUMP_FILE_SUFFIX = ".dump";

    private Map<Integer, BloomFilterHolder> bucketMap = new ConcurrentHashMap<>();

    private String path;

    private int buckets;
    private int maxKeys;
    private int foldFactor;
    private double errorRate;

    /**
     * bloom filter manager
     *
     * @param path       path for load and dump bloom filter data
     * @param buckets    bucket nums
     * @param maxKeys    elements num for each bucket
     * @param errorRate  false positive error rate
     * @param foldFactor fold this bloom to save space
     */
    public BloomFilterManager(String path, int buckets, int maxKeys, double errorRate, int foldFactor) {
        this.path = path;
        this.buckets = buckets;
        this.maxKeys = maxKeys;
        this.errorRate = errorRate;
        this.foldFactor = foldFactor;

        initDir();
        initHolder(bucketMap, true);
    }


    private void initDir() {
        File f = new File(path);
        if (!f.exists()) {
            f.mkdirs();
        }
    }

    /**
     * init each bucket holder and put to the map
     *
     * @param map
     * @param allocBloomByteBuffer
     */
    private void initHolder(Map<Integer, BloomFilterHolder> map, boolean allocBloomByteBuffer) {
        for (int i = 0; i < buckets; i++) {
            BloomFilterHolder holder = new BloomFilterHolder(i, maxKeys, errorRate, foldFactor, allocBloomByteBuffer);
            map.put(i, holder);
        }
    }

    /**
     * using mod to select bucket
     *
     * @param key
     * @return
     */
    private BloomFilterHolder getBloomHolder(byte[] key) {
        int mod = Math.abs(Arrays.hashCode(key)) % buckets;
        return bucketMap.get(mod);
    }

    /**
     * check whether this key is exist in this bloom filter
     *
     * @param key
     * @return
     */
    public boolean check(byte[] key) {
        return getBloomHolder(key).check(key);
    }

    /**
     * add an element to bloom filter
     *
     * @param key
     */
    public void add(byte[] key) {
        getBloomHolder(key).add(key);
    }

    /**
     * dump to file
     */
    public void dump() {
        for (BloomFilterHolder holder : bucketMap.values()) {
            if (holder != null) {
                try {
                    dumpByteBuffer(holder.byteBloomFilter.bloom, path + "/" + holder.getBucketId() + DUMP_FILE_SUFFIX);
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    private void dumpByteBuffer(ByteBuffer byteBuffer, String path) {
        byteBuffer.position(0);

        FileOutputStream fos = null;
        FileChannel fc = null;

        try {
            fos = new FileOutputStream(path);
            fc = fos.getChannel();

            while (byteBuffer.hasRemaining()) {
                fc.write(byteBuffer);
            }

            logger.info("dump bloom byte buffer to :" + path + " finished size is:" + fc.size());

        } catch (IOException e) {
            logger.error("", e);

        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    logger.error("", e);
                }
            }
        }
    }

    /**
     * load from file
     */
    public void load() {
        for (BloomFilterHolder holder : bucketMap.values()) {
            if (holder != null) {
                try {
                    ByteBuffer tmpBuffer = loadByteBuffer(path + "/" + holder.getBucketId() + DUMP_FILE_SUFFIX);
                    if (tmpBuffer != null) {
                        holder.byteBloomFilter.bloom = tmpBuffer;
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }
    }

    public void reload() {
        Map<Integer, BloomFilterHolder> folderMapTmp = new ConcurrentHashMap<>();

        initHolder(folderMapTmp, false);

        for (BloomFilterHolder holder : folderMapTmp.values()) {
            if (holder != null) {
                try {
                    ByteBuffer tmpBuffer = loadByteBuffer(path + "/" + holder.getBucketId() + DUMP_FILE_SUFFIX);
                    if (tmpBuffer == null) {
                        holder.byteBloomFilter.allocBloom();
                    } else {
                        holder.byteBloomFilter.bloom = tmpBuffer;
                    }
                } catch (Exception e) {
                    logger.error("", e);
                }
            }
        }

        this.bucketMap = folderMapTmp;

    }

    private ByteBuffer loadByteBuffer(String path) {
        File f = new File(path);
        if (!f.exists()) {
            logger.info("file not exist, load failed:" + path);
            return null;
        }

        FileInputStream fis = null;
        FileChannel fc = null;
        ByteBuffer tmpBuffer = null;
        long fSize;

        try {
            fis = new FileInputStream(path);
            fc = fis.getChannel();
            fSize = fc.size();

            tmpBuffer = ByteBuffer.allocate((int) fSize);

            int noOfBytesRead = fc.read(tmpBuffer);
            tmpBuffer.rewind();

            logger.info("Load BF from:" + path + " file size:" + fSize + " byte read size:" + noOfBytesRead);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fc != null) {
                try {
                    fc.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return tmpBuffer;
    }

}

