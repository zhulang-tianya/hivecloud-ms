package com.hivecloud.fault.core;

import com.hivecloud.fault.model.FaultRemovalRecord;

import java.util.List;

public interface FaultRemovalScheduler {

    void start();

    void stop();

    void registerStrategy(FaultRemovalStrategy strategy);

    List<FaultRemovalRecord> executeRemoval();
}
