package com.hivecloud.heartbeat.core;

import com.hivecloud.heartbeat.model.HeartbeatInfo;

public interface HeartbeatStore {

    void saveHeartbeat(HeartbeatInfo heartbeatInfo);

    HeartbeatInfo getHeartbeat(String instanceId);

    void removeHeartbeat(String instanceId);

    boolean exists(String instanceId);
}
