//package com.hivecloud.common.core.interceptor;
//
//import com.hivecloud.common.core.annotation.Idempotent;
//import com.hivecloud.common.core.exception.IdempotentException;
//import com.hivecloud.common.redis.util.RedisUtil;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.mock.web.MockHttpServletRequest;
//import org.springframework.web.context.request.RequestContextHolder;
//import org.springframework.web.context.request.ServletRequestAttributes;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * 幂等性拦截器单元测试
// *
// * @author HiveCloud Team
// * @date 2026-04-27
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("幂等性拦截器测试")
//class IdempotentInterceptorTest {
//
//    @Mock
//    private RedisUtil redisUtil;
//
//    @InjectMocks
//    private IdempotentInterceptor interceptor;
//
//    private MockHttpServletRequest request;
//
//    @BeforeEach
//    void setUp() {
//        request = new MockHttpServletRequest();
//        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
//    }
//
//    @Test
//    @DisplayName("首次请求应该成功执行")
//    void testFirstRequest() throws Throwable {
//        // 准备数据
//        when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(true);
//        when(redisUtil.delete(anyString())).thenReturn(true);
//
//        // 创建测试方法
//        TestService service = new TestService();
//        Object result = interceptor.invoke(service, "testMethod", "param1");
//
//        // 验证
//        assertNotNull(result);
//        verify(redisUtil, times(1)).setIfAbsent(anyString(), anyString(), anyLong());
//        verify(redisUtil, times(1)).delete(anyString());
//    }
//
//    @Test
//    @DisplayName("重复请求应该抛出幂等异常")
//    void testDuplicateRequest() {
//        // 准备数据
//        when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(false);
//
//        // 创建测试方法
//        TestService service = new TestService();
//
//        // 执行并验证
//        assertThrows(IdempotentException.class, () -> {
//            interceptor.invoke(service, "testMethod", "param1");
//        });
//
//        verify(redisUtil, times(1)).setIfAbsent(anyString(), anyString(), anyLong());
//        verify(redisUtil, never()).delete(anyString());
//    }
//
//    @Test
//    @DisplayName("允许重试时重复请求应该返回缓存结果")
//    void testAllowRetry() throws Throwable {
//        // 准备数据
//        when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(true);
//
//        // 创建测试方法
//        TestService service = new TestService();
//
//        // 第一次请求
//        Object result1 = interceptor.invoke(service, "testMethodWithRetry", "param1");
//        assertNotNull(result1);
//
//        // 第二次请求（允许重试）
//        when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(false);
//        Object result2 = interceptor.invoke(service, "testMethodWithRetry", "param1");
//        assertNotNull(result2);
//
//        // 验证幂等键未被删除
//        verify(redisUtil, never()).delete(anyString());
//    }
//
//    @Test
//    @DisplayName("业务执行失败应该删除幂等键")
//    void testBusinessException() {
//        // 准备数据
//        when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(true);
//        doThrow(new RuntimeException("业务异常")).when(redisUtil).delete(anyString());
//
//        // 创建测试方法
//        TestService service = new TestService();
//
//        // 执行并验证
//        assertThrows(RuntimeException.class, () -> {
//            interceptor.invoke(service, "testMethodWithException", "param1");
//        });
//
//        verify(redisUtil, times(1)).delete(anyString());
//    }
//
//    @Test
//    @DisplayName("SpEL 表达式应该正确解析方法参数")
//    void testSpelExpression() throws Throwable {
//        // 准备数据
//        when(redisUtil.setIfAbsent(anyString(), anyString(), anyLong())).thenReturn(true);
//        when(redisUtil.delete(anyString())).thenReturn(true);
//
//        // 创建测试方法
//        TestService service = new TestService();
//        Object result = interceptor.invoke(service, "testMethodWithParams", "user123", 100L);
//
//        // 验证
//        assertNotNull(result);
//        verify(redisUtil, times(1)).setIfAbsent(
//                eq("hivecloud:idempotent:test:user123:100"),
//                anyString(),
//                anyLong()
//        );
//    }
//
//    /**
//     * 测试服务类
//     */
//    static class TestService {
//
//        @Idempotent(key = "'test:' + #param")
//        public Object testMethod(String param) {
//            return "result-" + param;
//        }
//
//        @Idempotent(key = "'test:retry:' + #param", allowRetry = true)
//        public Object testMethodWithRetry(String param) {
//            return "result-" + param;
//        }
//
//        @Idempotent(key = "'test:exception:' + #param")
//        public Object testMethodWithException(String param) {
//            throw new RuntimeException("业务异常");
//        }
//
//        @Idempotent(key = "'test:' + #username + ':' + #amount")
//        public Object testMethodWithParams(String username, Long amount) {
//            return "result-" + username + "-" + amount;
//        }
//    }
//}
