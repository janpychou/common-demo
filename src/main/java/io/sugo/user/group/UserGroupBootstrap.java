package io.sugo.user.group;

import io.sugo.user.group.api.ApiServerHandler;
import io.sugo.user.group.utils.LogUtil;

public class UserGroupBootstrap {

    public static void main(String[] args) throws Exception {
        printUsage();
        SystemConfig.tryLock(SystemConfig.getLockFile());
        LogUtil.info("get lock file:" + SystemConfig.getLockFile());
        SystemConfig.parseArgs(args);
        ApiServerHandler.startApiServer();
    }

    private static void printUsage() {
        LogUtil.info("----------------usergroup -------------");
        LogUtil.info("----------------support args:");
        LogUtil.info("----------------        -bindip: bind on the ip, default value: 0.0.0.0");
        LogUtil.info("----------------        -port: listen on, default value: 2626");
        LogUtil.info("----------------eg: -bindip 172.19.16.36 -port 2626");
    }

}
