package com.hivecloud.fault.core;

import com.hivecloud.fault.config.FaultRemovalProperties;
import com.hivecloud.fault.model.FaultRemovalRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 连续失败故障摘除策略
 * 通过统计连续失败次数判断服务实例健康状态
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
public class ConsecutiveFailureRemovalStrategy extends BaseFaultRemovalStrategy {

    /**
     * 故障摘除配置属性，包含连续失败阈值等
     */
    private final FaultRemovalProperties properties;

    /**
     * 失败计数器，记录每个实例的连续失败次数
     * Key: 实例 ID，Value: 连续失败次数
     */
    private final Map<String, Integer> failureCounts = new ConcurrentHashMap<>();

    /**
     * 构造函数
     *
     * @param properties 故障摘除配置
     */
    public ConsecutiveFailureRemovalStrategy(FaultRemovalProperties properties) {
        this.properties = properties;
    }

    /**
     * 获取策略类型
     *
     * @return 策略类型标识
     */
    @Override
    public String getType() {
        return "CONSECUTIVE_FAILURE";
    }

    /**
     * 记录服务实例失败
     * 累加失败计数器，达到阈值后摘除实例
     *
     * @param instanceId 服务实例 ID
     * @param serviceId 服务 ID
     */
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

    /**
     * 记录服务实例成功
     * 重置失败计数器
     *
     * @param instanceId 服务实例 ID
     */
    public void recordSuccess(String instanceId) {
        failureCounts.remove(instanceId);
    }

    /**
     * 执行故障检测和摘除
     * 返回所有已摘除的故障实例记录
     *
     * @return 故障摘除记录列表
     */
    @Override
    public List<FaultRemovalRecord> detectAndRemove() {
        List<FaultRemovalRecord> removed = new ArrayList<>(removalRecords.values());
        return removed;
    }
}
