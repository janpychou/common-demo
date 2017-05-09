package io.sugo.user.group.io;

public interface DataIO {
    byte[] readFstByteFromRedis();

    void writeByteToRedisList(byte[] buf);

    boolean delete();
    void close();
}
