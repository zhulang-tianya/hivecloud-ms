package com.hivecloud.direct.connector.core;

import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * 域内直连客户端
 * 基于 OkHttp 实现服务间 HTTP 直连调用
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 */
@Slf4j
@Component
public class DirectConnector {

    /**
     * OkHttp 客户端
     */
    private OkHttpClient okHttpClient;

    /**
     * 初始化
     */
    @PostConstruct
    public void init() {
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectionPool(new ConnectionPool(16, 5, TimeUnit.MINUTES))
                .retryOnConnectionFailure(true)
                .build();

        log.info("域内直连客户端初始化完成");
    }

    /**
     * 销毁
     */
    @PreDestroy
    public void destroy() {
        if (okHttpClient != null && okHttpClient.connectionPool() != null) {
            okHttpClient.connectionPool().evictAll();
            log.info("域内直连客户端已关闭");
        }
    }

    /**
     * GET 请求
     *
     * @param url 请求 URL
     * @return 响应体
     * @throws IOException IO 异常
     */
    public String get(String url) throws IOException {
        log.debug("发起 GET 请求，url:{}", url);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                log.debug("GET 请求成功，url:{}, response:{}", url, responseBody);
                return responseBody;
            }
            throw new IOException("响应体为空");
        }
    }

    /**
     * POST 请求
     *
     * @param url 请求 URL
     * @param json JSON 数据
     * @return 响应体
     * @throws IOException IO 异常
     */
    public String post(String url, String json) throws IOException {
        log.debug("发起 POST 请求，url:{}, json:{}", url, json);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                log.debug("POST 请求成功，url:{}, response:{}", url, responseBody);
                return responseBody;
            }
            throw new IOException("响应体为空");
        }
    }

    /**
     * PUT 请求
     *
     * @param url 请求 URL
     * @param json JSON 数据
     * @return 响应体
     * @throws IOException IO 异常
     */
    public String put(String url, String json) throws IOException {
        log.debug("发起 PUT 请求，url:{}, json:{}", url, json);

        RequestBody body = RequestBody.create(json, MediaType.parse("application/json; charset=utf-8"));

        Request request = new Request.Builder()
                .url(url)
                .put(body)
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                log.debug("PUT 请求成功，url:{}, response:{}", url, responseBody);
                return responseBody;
            }
            throw new IOException("响应体为空");
        }
    }

    /**
     * DELETE 请求
     *
     * @param url 请求 URL
     * @return 响应体
     * @throws IOException IO 异常
     */
    public String delete(String url) throws IOException {
        log.debug("发起 DELETE 请求，url:{}", url);

        Request request = new Request.Builder()
                .url(url)
                .delete()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (response.body() != null) {
                String responseBody = response.body().string();
                log.debug("DELETE 请求成功，url:{}, response:{}", url, responseBody);
                return responseBody;
            }
            throw new IOException("响应体为空");
        }
    }
}
