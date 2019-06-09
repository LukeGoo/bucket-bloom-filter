package top.geyalu.bloomfilter.core;

import org.apache.hadoop.hbase.util.JenkinsHash;
import org.apache.hadoop.hbase.util.MurmurHash;

/**
 * Created by jamesou on 2016/9/19.
 */
public abstract class HashType {
    /**
     * Constant to denote invalid hash type.
     */
    public static final int INVALID_HASH = -1;
    /**
     * Constant to denote {@link JenkinsHash}.
     */
    public static final int JENKINS_HASH = 0;
    /**
     * Constant to denote {@link MurmurHash}.
     */
    public static final int MURMUR_HASH = 1;
}
