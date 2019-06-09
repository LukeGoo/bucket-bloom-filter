package top.geyalu.bloomfilter.service;

import org.apache.hadoop.hbase.util.Bytes;
import top.geyalu.bloomfilter.core.BloomFilterManager;

/**
 * @author Ge Yalu
 * created on 2019/5/24
 */
public class BloomFilterDemo {

    public static void main(String[] args) {

        //create a bloom filter manager with parameters: dump or load path,bucket numbers, each bucket contains elements, false positive rate, fold factor
        BloomFilterManager bloomFilterManager = new BloomFilterManager("/bloomfilter", 10, 10000, 0.01, 1);

        //load dump bloom filter data from files
        bloomFilterManager.load();


        //add any element to bloom filter using add method
        bloomFilterManager.add(Bytes.toBytes("test_1"));
        bloomFilterManager.add(Bytes.toBytes("test_2"));
        bloomFilterManager.add(Bytes.toBytes("test_3"));

        //check whether an element has been stored in the bloom filter using check method,true contains
        bloomFilterManager.check(Bytes.toBytes("test_1"));
        bloomFilterManager.check(Bytes.toBytes("test_2"));
        bloomFilterManager.check(Bytes.toBytes("test_3"));

        //false, not contains
        bloomFilterManager.check(Bytes.toBytes("test_4"));
        bloomFilterManager.check(Bytes.toBytes("test_5"));

        //dump bloom filter data to files
        bloomFilterManager.dump();
    }
}
