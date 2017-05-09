package io.sugo.user.group.io.redis;

import java.io.IOException;
import java.util.Set;

import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.util.SafeEncoder;

/**
 * Created by janpychou on 上午11:45. Mail: janpychou@qq.com
 */
public class RedisClientWrapper {
    private Jedis jedis;
    private JedisCluster cluster;
    private boolean clusterMode;

    public RedisClientWrapper(boolean clusterMode, Set<HostAndPort> nodes) {
        this.clusterMode = clusterMode;
        if (this.clusterMode) {
            if (cluster == null) {
                cluster = new JedisCluster(nodes, 10000, 10);
            }
        } else {
            if (jedis == null) {
                HostAndPort node = nodes.iterator().next();
                jedis = new Jedis(node.getHost(), node.getPort(), 10000, 10000);
            }
        }
    }

    public Long rpush(String listKey, byte[] dest) {
        if (clusterMode) {
            return cluster.rpush(SafeEncoder.encode(listKey), dest);
        } else {
            return jedis.rpush(SafeEncoder.encode(listKey), dest);
        }
    }

    public Long llen(String listKey) {
        if (clusterMode) {
            return cluster.llen(listKey);
        } else {
            return jedis.llen(listKey);
        }
    }

    public byte[] lindex(String listKey, long idx) {
        if (clusterMode) {
            return cluster.lindex(SafeEncoder.encode(listKey), idx);
        } else {
            return jedis.lindex(SafeEncoder.encode(listKey), idx);
        }
    }

    public Long del(String listKey) {
        if (clusterMode) {
            return cluster.del(listKey);
        } else {
            return jedis.del(listKey);
        }
    }

    public void close() {
        if (clusterMode) {
            if (cluster != null) {
                try {
                    cluster.close();
                } catch (IOException e) {
                    throw new RuntimeException("", e);
                }
            }
        } else {
            if (jedis != null) {
                jedis.close();
            }
        }
    }
}
