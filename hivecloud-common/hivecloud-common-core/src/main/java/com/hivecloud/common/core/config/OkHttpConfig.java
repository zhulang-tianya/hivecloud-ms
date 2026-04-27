package com.hivecloud.common.core.config;

import okhttp3.ConnectionPool;
import okhttp3.OkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * OkHttp 客户端配置
 * 配置连接池参数，优化 HTTP 请求性能
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Configuration
public class OkHttpConfig {

    /**
     * 最大空闲连接数
     */
    @Value("${okhttp.max-idle-connections:5}")
    private int maxIdleConnections;

    /**
     * 连接保活时间（分钟）
     */
    @Value("${okhttp.keep-alive-duration:5}")
    private int keepAliveDuration;

    /**
     * 连接超时时间（秒）
     */
    @Value("${okhttp.connect-timeout:10}")
    private int connectTimeout;

    /**
     * 读取超时时间（秒）
     */
    @Value("${okhttp.read-timeout:30}")
    private int readTimeout;

    /**
     * 写入超时时间（秒）
     */
    @Value("${okhttp.write-timeout:30}")
    private int writeTimeout;

    /**
     * 配置 OkHttp 连接池
     *
     * @return 连接池实例
     */
    @Bean
    public ConnectionPool connectionPool() {
        return new ConnectionPool(maxIdleConnections, keepAliveDuration, TimeUnit.MINUTES);
    }

    /**
     * 配置 OkHttpClient
     *
     * @param connectionPool 连接池
     * @return OkHttpClient 实例
     */
    @Bean
    public OkHttpClient okHttpClient(ConnectionPool connectionPool) {
        return new OkHttpClient.Builder()
                .connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS)
                .connectionPool(connectionPool)
                .retryOnConnectionFailure(true)
                .build();
    }
}
