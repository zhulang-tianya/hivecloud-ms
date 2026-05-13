package com.hivecloud.common.redis.util;

/**
 * Redis Key 前缀常量类
 * 
 * 统一规范 Redis Key 的命名，避免不同模块之间的 Key 冲突
 * 所有 Redis Key 都应使用此类定义的前缀
 * 
 * 命名规范：hivecloud:{module}:{type}:{identifier}
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
public final class RedisKeyPrefix {
    
    /**
     * 基础前缀
     */
    public static final String BASE = "hivecloud:";
    
    /**
     * 服务注册与发现模块前缀
     * 格式：hivecloud:registry:{type}:{serviceId}:{instanceId}
     */
    public static final String REGISTRY = BASE + "registry:";
    
    /**
     * 心跳检测模块前缀
     * 格式：hivecloud:heartbeat:{instanceId}
     */
    public static final String HEARTBEAT = BASE + "heartbeat:";
    
    /**
     * 缓存模块前缀
     * 格式：hivecloud:cache:{module}:{key}
     */
    public static final String CACHE = BASE + "cache:";
    
    /**
     * 验证码模块前缀
     * 格式：hivecloud:captcha:{uuid}
     */
    public static final String CAPTCHA = BASE + "captcha:";
    
    /**
     * 会话管理前缀
     * 格式：hivecloud:session:{type}:{token}
     */
    public static final String SESSION = BASE + "session:";
    
    /**
     * 限流控制前缀
     * 格式：hivecloud:ratelimit:{api}:{identifier}
     */
    public static final String RATE_LIMIT = BASE + "ratelimit:";
    
    /**
     * 分布式锁前缀
     * 格式：hivecloud:lock:{resource}:{identifier}
     */
    public static final String LOCK = BASE + "lock:";
    
    /**
     * Gossip 集群同步前缀
     * 格式：hivecloud:gossip:{type}:{nodeId}
     */
    public static final String GOSSIP = BASE + "gossip:";
    
    /**
     * 故障剔除记录前缀
     * 格式：hivecloud:fault:{type}:{instanceId}
     */
    public static final String FAULT = BASE + "fault:";
    
    /**
     * 业务链路追踪前缀
     * 格式：hivecloud:business:{type}:{traceId}
     */
    public static final String BUSINESS = BASE + "business:";
    
    /**
     * 事件总线前缀
     * 格式：hivecloud:event:{type}:{eventId}
     */
    public static final String EVENT = BASE + "event:";
    
    /**
     * 插件配置前缀
     * 格式：hivecloud:plugin:{name}:{key}
     */
    public static final String PLUGIN = BASE + "plugin:";
    
    /**
     * 私有构造函数，防止实例化
     */
    private RedisKeyPrefix() {
        throw new IllegalStateException("Utility class cannot be instantiated");
    }
}
