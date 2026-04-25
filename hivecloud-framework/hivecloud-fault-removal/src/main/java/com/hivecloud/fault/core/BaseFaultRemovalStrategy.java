package com.hivecloud.fault.core;

import com.hivecloud.fault.model.FaultRemovalRecord;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class BaseFaultRemovalStrategy implements FaultRemovalStrategy {

    protected final Map<String, FaultRemovalRecord> removalRecords = new ConcurrentHashMap<>();

    public void recordRemoval(FaultRemovalRecord record) {
        removalRecords.put(record.getInstanceId(), record);
    }

    public List<FaultRemovalRecord> getRemovalRecords() {
        return new ArrayList<>(removalRecords.values());
    }

    public void clearRecords() {
        removalRecords.clear();
    }

    protected FaultRemovalRecord createRecord(String instanceId, String serviceId, String reason, String type) {
        FaultRemovalRecord record = new FaultRemovalRecord();
        record.setInstanceId(instanceId);
        record.setServiceId(serviceId);
        record.setReason(reason);
        record.setRemovalType(type);
        record.setRemovalTime(LocalDateTime.now());
        return record;
    }
}
