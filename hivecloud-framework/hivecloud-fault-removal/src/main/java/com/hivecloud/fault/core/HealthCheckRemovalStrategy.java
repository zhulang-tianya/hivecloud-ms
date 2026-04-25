package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class HealthCheckRemovalStrategy extends BaseFaultRemovalStrategy {

    private final FaultRemovalProperties properties;

    private final RestTemplate restTemplate;

    private final Map<String, Integer> healthCheckFailures = new ConcurrentHashMap<>();

    public HealthCheckRemovalStrategy(FaultRemovalProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    @Override
    public String getType() {
        return "HEALTH_CHECK";
    }

    public void checkInstance(String instanceId, String serviceId, String ip, Integer port) {
        String healthUrl = "http://" + ip + ":" + port + properties.getHealthCheckPath();
        try {
            String response = restTemplate.getForObject(healthUrl, String.class);
            if (response != null) {
                healthCheckFailures.remove(instanceId);
            }
        } catch (Exception e) {
            int failures = healthCheckFailures.merge(instanceId, 1, Integer::sum);
            if (failures >= properties.getHealthCheckFailureThreshold()) {
                FaultRemovalRecord record = createRecord(
                        instanceId,
                        serviceId,
                        "Health check failed " + failures + " times",
                        "HEALTH_CHECK"
                );
                record.setConsecutiveFailures(failures);
                recordRemoval(record);
                healthCheckFailures.remove(instanceId);
                log.warn("Instance {} removed due to health check failures", instanceId);
            }
        }
    }

    @Override
    public List<FaultRemovalRecord> detectAndRemove() {
        return new ArrayList<>(removalRecords.values());
    }
}
