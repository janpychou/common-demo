package io.sugo.user.group.io.redis;

import java.nio.ByteBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import io.sugo.user.group.api.dto.DataConfig;
import io.sugo.user.group.io.DataIO;
import redis.clients.jedis.HostAndPort;

public class RedisDataIO implements DataIO {
    private final static int BATCH_SIZE = 1 * 1024 * 1024;
    private final static int DIRECT_BUFFER_SIZE = 100 * 1024 * 1024;
    private static final int MAX_SIZE = 5;

    private final String hostAndPorts;
    private String groupId;
    private boolean clusterMode = false;
    private Set<HostAndPort> nodes;
    private BlockingQueue<RedisClientWrapper> queue = new LinkedBlockingQueue<>();
    private final AtomicInteger size = new AtomicInteger();
    private Object lock = new Object();

    public RedisDataIO(String hostAndPorts, boolean clusterMode, String groupId) {
        this.clusterMode = clusterMode;
        this.hostAndPorts = hostAndPorts;
        parseHostAndPorts(hostAndPorts);
        this.groupId = groupId;
    }

    public RedisDataIO(DataConfig config) {
        this(config.getHostAndPorts(), config.getClusterMode(), config.getGroupId());
    }

    private void parseHostAndPorts(String hostAndPorts) {
        StringTokenizer tokenizer = new StringTokenizer(hostAndPorts, ",;");
        String token;
        String[] tmp;
        this.nodes = new HashSet<>();
        while (tokenizer.hasMoreTokens()) {
            token = tokenizer.nextToken();
            tmp = token.split(":");
            this.nodes.add(new HostAndPort(tmp[0], Integer.valueOf(tmp[1])));
        }
    }

    public Set<HostAndPort> getNodes() {
        return nodes;
    }

    private void releaseRedisClientWrapper(RedisClientWrapper wrapper) {
        try {
            queue.put(wrapper);
        } catch (InterruptedException e) {
            throw new RuntimeException("", e);
        }
    }

    private RedisClientWrapper getRedisClientWrapper() {
        RedisClientWrapper wrapper;
        synchronized (lock) {
            if (queue.size() == 0 && size.get() < MAX_SIZE) {
                wrapper = new RedisClientWrapper(clusterMode, getNodes());
                size.incrementAndGet();
            } else {
                try {
                    wrapper = queue.take();
                } catch (InterruptedException e) {
                    throw new RuntimeException("", e);
                }
            }
        }

        return wrapper;
    }

    @Override
    public byte[] readFstByteFromRedis() {
        RedisClientWrapper wrapper = getRedisClientWrapper();
        Long listSize = wrapper.llen(groupId);
        if (listSize == 0) {
            releaseRedisClientWrapper(wrapper);
            return new byte[0];
        }

        ByteBuffer buf = ByteBuffer.allocate(DIRECT_BUFFER_SIZE);

        byte[] bytes;
        buf.clear();
        for (int i = 0; i < listSize; i++) {
            byte[] item = wrapper.lindex(groupId, i);
            buf.put(item);
        }

        buf.flip();
        bytes = new byte[buf.limit()];
        buf.get(bytes);
        releaseRedisClientWrapper(wrapper);

        return bytes;
    }

    @Override
    public void writeByteToRedisList(byte[] buf) {
        RedisClientWrapper wrapper = getRedisClientWrapper();
        byte[] dest = new byte[BATCH_SIZE];
        int srcPos = 0;
        int length = BATCH_SIZE;
        int totalSize = buf.length;
        wrapper.del(groupId);
        while (srcPos < buf.length) {
            if (length > totalSize - srcPos) {
                length = totalSize - srcPos;
                dest = new byte[length];
            }
            System.arraycopy(buf, srcPos, dest, 0, length);
            srcPos += length;
            wrapper.rpush(groupId, dest);
        }
        releaseRedisClientWrapper(wrapper);
    }

    @Override
    public boolean delete() {
        RedisClientWrapper wrapper = getRedisClientWrapper();
        wrapper.del(groupId);
        return true;
    }

    @Override
    public void close() {
        while (queue.size() > 0) {
            try {
                RedisClientWrapper wrapper = queue.poll(3, TimeUnit.SECONDS);
                wrapper.close();
            } catch (InterruptedException e) {
                throw new RuntimeException("close redis client error", e);
            }
        }
    }

    @Override
    public String toString() {
        return RedisDataIO.class.getSimpleName() + "{" + "nodes='" + hostAndPorts + '\'' + ", groupId='" + groupId
                + '\'' + ", clusterMode='" + clusterMode + '\'' + '}';
    }

    @Override
    public int hashCode() {
        int result = (hostAndPorts != null ? hostAndPorts.hashCode() : 0);
        result = 31 * result + (groupId != null ? groupId.hashCode() : 0);
        result = 31 * result + (clusterMode ? 1 : 0);
        return result;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        RedisDataIO that = (RedisDataIO) o;
        if (hostAndPorts != null ? !hostAndPorts.equals(that.hostAndPorts) : that.hostAndPorts != null) {
            return false;
        }
        if (groupId != null ? !groupId.equals(that.groupId) : that.groupId != null) {
            return false;
        }
        if (clusterMode != that.clusterMode) {
            return false;
        }
        return true;
    }

}
