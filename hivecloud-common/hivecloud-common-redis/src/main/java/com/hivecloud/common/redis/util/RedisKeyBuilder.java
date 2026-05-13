package com.hivecloud.common.redis.util;

import java.util.StringJoiner;

/**
 * Redis Key 构建器
 * 
 * 提供统一的 Redis Key 构建方法，确保 Key 命名规范一致
 * 所有 Redis Key 的构建都应使用此类提供的方法
 * 
 * 使用示例：
 * <pre>
 * // 构建缓存 Key
 * String cacheKey = RedisKeyBuilder.cache("user", "1001");
 * // 结果：hivecloud:cache:user:1001
 * 
 * // 构建服务注册 Key
 * String registryKey = RedisKeyBuilder.registry("user-service", "i-001");
 * // 结果：hivecloud:registry:meta:user-service:i-001
 * 
 * // 构建验证码 Key
 * String captchaKey = RedisKeyBuilder.captcha("550e8400-e29b-41d4-a716-446655440000");
 * // 结果：hivecloud:captcha:550e8400-e29b-41d4-a716-446655440000
 * </pre>
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
public final class RedisKeyBuilder {
    
    /**
     * 构建缓存 Key
     * 格式：hivecloud:cache:{module}:{key}
     * 
     * @param module 模块名称（如：user, order, product）
     * @param key 缓存键（如：用户 ID、订单号）
     * @return 完整的 Redis Key
     */
    public static String cache(String module, String key) {
        validateNotBlank(module, "module");
        validateNotBlank(key, "key");
        return join(RedisKeyPrefix.CACHE, module, key);
    }
    
    /**
     * 构建配置缓存 Key
     * 格式：hivecloud:cache:config:{key}
     * 
     * @param key 配置键
     * @return 完整的 Redis Key
     */
    public static String config(String key) {
        validateNotBlank(key, "config key");
        return join(RedisKeyPrefix.CACHE, "config", key);
    }
    
    /**
     * 构建服务注册元数据 Key
     * 格式：hivecloud:registry:meta:{serviceId}:{instanceId}
     * 
     * @param serviceId 服务 ID（如：user-service）
     * @param instanceId 实例 ID（如：i-001）
     * @return 完整的 Redis Key
     */
    public static String registry(String serviceId, String instanceId) {
        validateNotBlank(serviceId, "serviceId");
        validateNotBlank(instanceId, "instanceId");
        return join(RedisKeyPrefix.REGISTRY, "meta", serviceId, instanceId);
    }
    
    /**
     * 构建服务集合 Key
     * 格式：hivecloud:registry:set:{serviceId}
     * 
     * @param serviceId 服务 ID
     * @return 完整的 Redis Key
     */
    public static String registrySet(String serviceId) {
        validateNotBlank(serviceId, "serviceId");
        return RedisKeyPrefix.REGISTRY + "set:" + serviceId;
    }
    
    /**
     * 构建心跳 Key
     * 格式：hivecloud:heartbeat:{instanceId}
     * 
     * @param instanceId 实例 ID
     * @return 完整的 Redis Key
     */
    public static String heartbeat(String instanceId) {
        validateNotBlank(instanceId, "instanceId");
        return RedisKeyPrefix.HEARTBEAT + instanceId;
    }
    
    /**
     * 构建验证码 Key
     * 格式：hivecloud:captcha:{uuid}
     * 
     * @param uuid 验证码 UUID
     * @return 完整的 Redis Key
     */
    public static String captcha(String uuid) {
        validateNotBlank(uuid, "uuid");
        return RedisKeyPrefix.CAPTCHA + uuid;
    }
    
    /**
     * 构建会话 Key
     * 格式：hivecloud:session:{type}:{token}
     * 
     * @param type 会话类型（如：user, admin）
     * @param token 会话令牌
     * @return 完整的 Redis Key
     */
    public static String session(String type, String token) {
        validateNotBlank(type, "session type");
        validateNotBlank(token, "token");
        return join(RedisKeyPrefix.SESSION, type, token);
    }
    
    /**
     * 构建限流 Key
     * 格式：hivecloud:ratelimit:{api}:{identifier}
     * 
     * @param api API 标识（如：/api/user/list）
     * @param identifier 限流标识（如：用户 ID、IP 地址）
     * @return 完整的 Redis Key
     */
    public static String rateLimit(String api, String identifier) {
        validateNotBlank(api, "api");
        validateNotBlank(identifier, "identifier");
        return join(RedisKeyPrefix.RATE_LIMIT, api.replaceAll("/", ":"), identifier);
    }
    
    /**
     * 构建分布式锁 Key
     * 格式：hivecloud:lock:{resource}:{identifier}
     * 
     * @param resource 资源名称（如：order:create）
     * @param identifier 锁标识（如：订单 ID）
     * @return 完整的 Redis Key
     */
    public static String lock(String resource, String identifier) {
        validateNotBlank(resource, "resource");
        validateNotBlank(identifier, "identifier");
        return join(RedisKeyPrefix.LOCK, resource, identifier);
    }
    
    /**
     * 构建 Gossip 节点 Key
     * 格式：hivecloud:gossip:{type}:{nodeId}
     * 
     * @param type 类型（如：node, state）
     * @param nodeId 节点 ID
     * @return 完整的 Redis Key
     */
    public static String gossip(String type, String nodeId) {
        validateNotBlank(type, "type");
        validateNotBlank(nodeId, "nodeId");
        return join(RedisKeyPrefix.GOSSIP, type, nodeId);
    }
    
    /**
     * 构建故障记录 Key
     * 格式：hivecloud:fault:{type}:{instanceId}
     * 
     * @param type 故障类型（如：consecutive, timeout）
     * @param instanceId 实例 ID
     * @return 完整的 Redis Key
     */
    public static String fault(String type, String instanceId) {
        validateNotBlank(type, "fault type");
        validateNotBlank(instanceId, "instanceId");
        return join(RedisKeyPrefix.FAULT, type, instanceId);
    }
    
    /**
     * 构建业务链路 Key
     * 格式：hivecloud:business:{type}:{traceId}
     * 
     * @param type 业务类型（如：order, payment）
     * @param traceId 链路追踪 ID
     * @return 完整的 Redis Key
     */
    public static String business(String type, String traceId) {
        validateNotBlank(type, "business type");
        validateNotBlank(traceId, "traceId");
        return join(RedisKeyPrefix.BUSINESS, type, traceId);
    }
    
    /**
     * 构建事件 Key
     * 格式：hivecloud:event:{type}:{eventId}
     * 
     * @param type 事件类型（如：order-created, payment-success）
     * @param eventId 事件 ID
     * @return 完整的 Redis Key
     */
    public static String event(String type, String eventId) {
        validateNotBlank(type, "event type");
        validateNotBlank(eventId, "eventId");
        return join(RedisKeyPrefix.EVENT, type, eventId);
    }
    
    /**
     * 构建插件配置 Key
     * 格式：hivecloud:plugin:{name}:{key}
     * 
     * @param name 插件名称
     * @param key 配置键
     * @return 完整的 Redis Key
     */
    public static String plugin(String name, String key) {
        validateNotBlank(name, "plugin name");
        validateNotBlank(key, "config key");
        return join(RedisKeyPrefix.PLUGIN, name, key);
    }
    
    /**
     * 通用的 Key 连接方法
     * 使用冒号分隔各个部分
     * 
     * @param parts Key 的各个部分
     * @return 连接后的完整 Key
     */
    private static String join(String... parts) {
        StringJoiner joiner = new StringJoiner(":");
        for (String part : parts) {
            if (part != null && !part.isEmpty()) {
                joiner.add(part);
            }
        }
        return joiner.toString();
    }
    
    /**
     * 验证字符串不为空
     * 
     * @param value 待验证的字符串
     * @param fieldName 字段名称
     * @throws IllegalArgumentException 如果字符串为空
     */
    private static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be blank");
        }
    }
    
    /**
     * 私有构造函数，防止实例化
     */
    private RedisKeyBuilder() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }
}
