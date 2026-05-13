package com.hivecloud.common.redis.config;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis 通用配置类
 * 
 * 提供多种类型的 RedisTemplate，适配不同场景：
 * - StringRedisTemplate：字符串操作（验证码、简单KV等）
 * - RedisTemplate<String, Object>：对象操作（用户信息、复杂数据结构等）
 * 
 * @author HiveCloud Team
 */
@Slf4j
@Configuration
public class RedisConfig {

    /**
     * 配置 RedisConnectionFactory
     * 使用 Environment 动态读取配置，确保获取最新的 Nacos 配置
     */
    @Bean
    @Primary
    public LettuceConnectionFactory redisConnectionFactory(Environment environment) {
        // 从 Environment 直接读取，确保获取最新配置（包括 Nacos 配置）
        String host = environment.getProperty("spring.redis.host", "localhost");
        int port = Integer.parseInt(environment.getProperty("spring.redis.port", "6379"));
        String password = environment.getProperty("spring.redis.password");
        int database = Integer.parseInt(environment.getProperty("spring.redis.database", "0"));
        
        log.info("========================================");
        log.info("创建 RedisConnectionFactory:");
        log.info("  host: {}", host);
        log.info("  port: {}", port);
        log.info("  password: {}", password != null ? "******" : "null");
        log.info("  database: {}", database);
        log.info("========================================");
        
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        
        if (password != null && !password.isEmpty()) {
            config.setPassword(password);
        }
        
        config.setDatabase(database);
        
        return new LettuceConnectionFactory(config);
    }

    /**
     * 配置 String 类型的 StringRedisTemplate
     * 适用于：验证码、简单KV存储、计数器等场景
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        StringRedisTemplate template = new StringRedisTemplate();
        template.setConnectionFactory(connectionFactory);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 RedisTemplate<String, String>
     * 适用于：需要 RedisTemplate<String, String> 类型注入的场景（如 CaptchaUtil）
     * 使用 @Primary 标记为首选 Bean，解决与 StringRedisTemplate 的类型冲突
     */
    @Bean
    @Primary
    public RedisTemplate<String, String> redisStringTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        
        StringRedisSerializer serializer = new StringRedisSerializer();
        template.setKeySerializer(serializer);
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(serializer);
        template.setHashValueSerializer(serializer);
        
        template.afterPropertiesSet();
        return template;
    }

    /**
     * 配置 Object 类型的 RedisTemplate
     * 适用于：对象存储、复杂数据结构等场景
     * 使用 JSON 序列化，支持 Java 8 时间类型
     * 注意：safeRedisTemplate 已标记为 @Primary，如需使用此模板请通过 @Qualifier("redisTemplate") 显式指定
     */
    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 配置 JSON 序列化器
        Jackson2JsonRedisSerializer<Object> jsonSerializer = createJsonSerializer();
        
        // 配置 String 序列化器
        StringRedisSerializer stringSerializer = new StringRedisSerializer();

        // Key 使用 String 序列化
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        
        // Value 使用 JSON 序列化
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }

    /**
     * 创建 JSON 序列化器
     * 支持 Java 8 LocalDateTime/LocalDate/LocalTime 等时间类型
     */
    private Jackson2JsonRedisSerializer<Object> createJsonSerializer() {
        // 使用新的构造函数，直接传入 ObjectMapper
        ObjectMapper objectMapper = new ObjectMapper();
        // 配置可见性
        objectMapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        // 禁用将日期写为时间戳
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // 注册 Java 8 时间模块
        objectMapper.registerModule(new JavaTimeModule());
        
        return new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);
    }
}