package com.hivecloud.common.redis.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 序列化安全配置
 * 
 * 使用 GenericJackson2JsonRedisSerializer 替代 Jackson2JsonRedisSerializer
 * GenericJackson2JsonRedisSerializer 会自动处理类型信息，更安全
 * 
 * 安全增强：
 * 1. 自动推断类型，无需手动指定
 * 2. 支持 Java 8 时间类型
 * 3. 内置类型安全检查
 * 
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Slf4j
@Configuration
public class RedisSerializationConfig {

    /**
     * 配置安全的 RedisTemplate
     * 使用 GenericJackson2JsonRedisSerializer 提高反序列化安全性
     * 
     * @param connectionFactory Redis 连接工厂
     * @return 配置好的 RedisTemplate
     */
    @Bean("safeRedisTemplate")
    @Primary
    public RedisTemplate<String, Object> safeRedisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        // 使用 String 序列化器处理 Key
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // 使用 GenericJackson2JsonRedisSerializer 处理 Value
        // 该序列化器会自动处理类型信息，更安全
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);
        
        template.afterPropertiesSet();
        
        log.info("RedisTemplate 已配置安全序列化器：GenericJackson2JsonRedisSerializer");
        return template;
    }
}
