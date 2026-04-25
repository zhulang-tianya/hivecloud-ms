package com.hivecloud.fault.core;

import com.hivecloud.fault.model.FaultRemovalRecord;

import java.util.List;

public interface FaultRemovalStrategy {

    String getType();

    List<FaultRemovalRecord> detectAndRemove();
}
