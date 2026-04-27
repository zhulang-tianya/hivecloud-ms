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

/**
 * 健康检查故障摘除策略
 * 通过 HTTP 健康检查接口检测服务实例健康状态
 * 连续失败达到阈值后自动摘除故障实例
 * 继承基类：BaseFaultRemovalStrategy
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see BaseFaultRemovalStrategy
 * @see FaultRemovalProperties
 * @see FaultRemovalRecord
 */
@Slf4j
@Component
public class HealthCheckRemovalStrategy extends BaseFaultRemovalStrategy {

    /**
     * 故障摘除配置属性，包含健康检查路径、失败阈值等
     */
    private final FaultRemovalProperties properties;

    /**
     * REST 模板，用于 HTTP 健康检查请求
     */
    private final RestTemplate restTemplate;

    /**
     * 健康检查失败计数器，记录每个实例的连续失败次数
     * Key: 实例 ID，Value: 连续失败次数
     */
    private final Map<String, Integer> healthCheckFailures = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param properties 故障摘除配置
     * @param restTemplate REST 通信模板
     */
    public HealthCheckRemovalStrategy(FaultRemovalProperties properties, RestTemplate restTemplate) {
        this.properties = properties;
        this.restTemplate = restTemplate;
    }

    /**
     * 获取策略类型
     *
     * @return 策略类型标识
     */
    @Override
    public String getType() {
        return "HEALTH_CHECK";
    }

    /**
     * 检查服务实例健康状态
     * 调用健康检查接口，失败时累加计数器，达到阈值后摘除实例
     *
     * @param instanceId 服务实例 ID
     * @param serviceId 服务 ID
     * @param ip 服务实例 IP
     * @param port 服务实例端口
     */
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

    /**
     * 执行故障检测和摘除
     * 返回所有已摘除的故障实例记录
     *
     * @return 故障摘除记录列表
     */
    @Override
    public List<FaultRemovalRecord> detectAndRemove() {
        return new ArrayList<>(removalRecords.values());
    }
}
