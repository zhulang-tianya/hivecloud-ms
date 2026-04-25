package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class ConsecutiveFailureRemovalStrategy extends BaseFaultRemovalStrategy {

    private final FaultRemovalProperties properties;

    private final Map<String, Integer> failureCounts = new ConcurrentHashMap<>();

    public ConsecutiveFailureRemovalStrategy(FaultRemovalProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getType() {
        return "CONSECUTIVE_FAILURE";
    }

    public void recordFailure(String instanceId, String serviceId) {
        failureCounts.merge(instanceId, 1, Integer::sum);
        int count = failureCounts.get(instanceId);
        if (count >= properties.getConsecutiveFailureThreshold()) {
            FaultRemovalRecord record = createRecord(
                    instanceId,
                    serviceId,
                    "Consecutive failures reached " + count,
                    "CONSECUTIVE_FAILURE"
            );
            record.setConsecutiveFailures(count);
            recordRemoval(record);
            failureCounts.remove(instanceId);
            log.warn("Instance {} removed due to {} consecutive failures", instanceId, count);
        }
    }

    public void recordSuccess(String instanceId) {
        failureCounts.remove(instanceId);
    }

    @Override
    public List<FaultRemovalRecord> detectAndRemove() {
        List<FaultRemovalRecord> removed = new ArrayList<>(removalRecords.values());
        return removed;
    }
}
