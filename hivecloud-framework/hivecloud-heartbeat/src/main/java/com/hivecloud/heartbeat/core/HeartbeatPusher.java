package com.hivecloud.heartbeat.core;

import com.hivecloud.heartbeat.model.HeartbeatInfo;

public interface HeartbeatPusher {

    void start();

    void stop();

    void pushHeartbeat(HeartbeatInfo heartbeatInfo);
}
