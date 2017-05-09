package io.sugo.user.group.io;

import io.sugo.user.group.api.dto.DataConfig;
import io.sugo.user.group.io.redis.RedisDataIO;

public class DataIOFactory {

    private static final String REDIS = "redis";

    public static DataIO create(DataConfig dataConfig) {
        if (REDIS.equalsIgnoreCase(dataConfig.getType())) {
            DataIO dataIo = new RedisDataIO(dataConfig);
            return dataIo;
        }
        return null;
    }
}
