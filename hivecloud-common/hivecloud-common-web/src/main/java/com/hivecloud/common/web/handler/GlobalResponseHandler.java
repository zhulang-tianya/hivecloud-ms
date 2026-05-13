package com.hivecloud.common.web.handler;

import com.hivecloud.common.core.result.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

/**
 * 全局响应包装器
 * <p>
 * 自动将所有 Controller 返回的结果包装成统一的 Result 格式
 * 使前端可以统一处理响应数据
 * </p>
 *
 * @author HiveCloud Team
 * @date 2026-05-13
 */
@Slf4j
@ControllerAdvice
@RequiredArgsConstructor
public class GlobalResponseHandler implements ResponseBodyAdvice<Object> {

    private final ObjectMapper objectMapper;

    /**
     * 判断是否需要包装响应
     * <p>
     * 对于已包装为 Result 的响应，不再重复包装
     * 对于其他类型的响应，进行包装
     * </p>
     *
     * @param returnType    返回类型
     * @param converterType 转换器类型
     * @return true 需要包装，false 不需要包装
     */
    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 如果返回类型已经是 Result，则不需要包装
        return !returnType.getParameterType().isAssignableFrom(Result.class);
    }

    /**
     * 包装响应数据
     * <p>
     * 将原始响应数据包装成 Result 格式
     * </p>
     *
     * @param body                  原始响应数据
     * @param returnType            返回类型
     * @param selectedContentType   内容类型
     * @param selectedConverterType 转换器类型
     * @param request               请求对象
     * @param response              响应对象
     * @return 包装后的 Result 对象
     */
    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        
        // 如果返回值为空，返回成功响应
        if (body == null) {
            return Result.success();
        }
        
        // 如果已经是 Result 类型，直接返回（理论上不会走到这里）
        if (body instanceof Result) {
            return body;
        }
        
        // 如果是 String 类型，需要特殊处理，避免转换错误
        if (body instanceof String) {
            try {
                // 将字符串解析为对象，如果已经是 JSON 格式的 Result，则直接返回
                if (isValidJson((String) body)) {
                    Object parsed = objectMapper.readValue((String) body, Object.class);
                    if (parsed instanceof Result) {
                        return body;
                    }
                }
                // 如果是普通字符串，包装成 Result
                return objectMapper.writeValueAsString(Result.success(body));
            } catch (Exception e) {
                log.warn("Failed to wrap string response: {}", e.getMessage());
                return Result.success(body);
            }
        }
        
        // 其他类型，直接包装成 Result
        return Result.success(body);
    }

    /**
     * 判断字符串是否为有效的 JSON
     */
    private boolean isValidJson(String json) {
        if (json == null || json.isBlank()) {
            return false;
        }
        json = json.trim();
        return (json.startsWith("{") && json.endsWith("}")) ||
               (json.startsWith("[") && json.endsWith("]"));
    }
}