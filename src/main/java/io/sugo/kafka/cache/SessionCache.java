package io.sugo.kafka.cache;

import com.google.common.collect.Maps;
import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

/**
 * Created by gary on 16-10-26.
 */
public class SessionCache {
    private Map<String, Session> sessionMap = Maps.newHashMap();
    private Map<String, String> deviceMap = Maps.newHashMap();
    private Random rdint = new Random();

    public String getDeviceId(String userId) {
        String deviceId = deviceMap.get(userId);
        if (StringUtils.isEmpty(deviceId)){
            deviceId = UUID.randomUUID().toString();
        }
        return deviceId;
    }

    public String getSessionId(String userId) {
        Session session = sessionMap.get(userId);
        if (sessionMap.get(userId) != null) {
            if ((session.timeStamp + session.exsistSecs * 1000) > System.currentTimeMillis()) {
                return session.sessionId;
            } else {

            }

        }
        session = new Session(UUID.randomUUID().toString(), new Date().getTime(), rdint.nextInt(5200)+2000);
        sessionMap.put(userId, session);
        return session.sessionId;
    }


    public class Session {
        public Session() {}

        public Session(String sessionId, long timeStamp, int exsistSecs) {
            this.sessionId = sessionId;
            this.timeStamp = timeStamp;
            this.exsistSecs = exsistSecs;
        }

        private String sessionId;
        private long timeStamp;
        private int exsistSecs;
    }

}
