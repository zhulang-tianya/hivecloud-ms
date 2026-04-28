package com.hivecloud.module.claim.service.impl;

import com.hivecloud.module.claim.entity.ClaimEntity;
import com.hivecloud.module.claim.mapper.ClaimMapper;
import com.hivecloud.module.claim.service.ClaimService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * ClaimServiceImpl 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimServiceImpl 理赔服务测试")
class ClaimServiceImplTest {

    @Mock
    private ClaimMapper claimMapper;

    @InjectMocks
    private ClaimServiceImpl claimService;

    private ClaimEntity testClaim;

    @BeforeEach
    @DisplayName("测试前置准备 - 创建测试数据")
    void setUp() {
        testClaim = new ClaimEntity();
        testClaim.setId(1L);
        testClaim.setUserId(100L);
        testClaim.setClaimType("medical");
        testClaim.setClaimAmount(new BigDecimal("5000.00"));
        testClaim.setRemark("测试理赔申请");
    }

    @Test
    @DisplayName("测试 createClaim 方法 - 创建理赔申请成功")
    void testCreateClaimSuccess() {
        when(claimMapper.insert(any(ClaimEntity.class))).thenReturn(1);

        String claimNo = claimService.createClaim(testClaim);

        assertNotNull(claimNo);
        assertTrue(claimNo.startsWith("CLAIM"));
        verify(claimMapper, times(1)).insert(any(ClaimEntity.class));

        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
        verify(claimMapper).insert(captor.capture());
        ClaimEntity capturedClaim = captor.getValue();

        assertNotNull(capturedClaim.getClaimNo());
        assertEquals(0, capturedClaim.getClaimStatus());
        assertNotNull(capturedClaim.getApplyTime());
        assertNotNull(capturedClaim.getCreateTime());
    }

    @Test
    @DisplayName("测试 getClaim 方法 - 查询理赔申请成功")
    void testGetClaimSuccess() {
        String claimNo = "CLAIM123456789";
        when(claimMapper.selectOne(any())).thenReturn(testClaim);

        ClaimEntity result = claimService.getClaim(claimNo);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("medical", result.getClaimType());
        assertEquals(new BigDecimal("5000.00"), result.getClaimAmount());
        verify(claimMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试 getClaim 方法 - 理赔申请不存在")
    void testGetClaimNotFound() {
        String claimNo = "CLAIM_NOT_EXIST";
        when(claimMapper.selectOne(any())).thenReturn(null);

        ClaimEntity result = claimService.getClaim(claimNo);

        assertNull(result);
        verify(claimMapper, times(1)).selectOne(any());
    }

    @Test
    @DisplayName("测试 auditClaim 方法 - 审核通过")
    void testAuditClaimApproved() {
        String claimNo = "CLAIM123";
        Long auditorId = 999L;
        String opinion = "审核通过";

        when(claimMapper.selectOne(any())).thenReturn(testClaim);
        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);

        claimService.auditClaim(claimNo, auditorId, opinion, true);

        verify(claimMapper, times(1)).updateById(any(ClaimEntity.class));

        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
        verify(claimMapper).updateById(captor.capture());
        ClaimEntity capturedClaim = captor.getValue();

        assertEquals(2, capturedClaim.getClaimStatus());
        assertEquals(auditorId, capturedClaim.getAuditorId());
        assertEquals(opinion, capturedClaim.getAuditOpinion());
        assertNotNull(capturedClaim.getAuditTime());
    }

    @Test
    @DisplayName("测试 auditClaim 方法 - 审核拒绝")
    void testAuditClaimRejected() {
        String claimNo = "CLAIM123";
        Long auditorId = 999L;
        String opinion = "审核拒绝";

        when(claimMapper.selectOne(any())).thenReturn(testClaim);
        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);

        claimService.auditClaim(claimNo, auditorId, opinion, false);

        verify(claimMapper, times(1)).updateById(any(ClaimEntity.class));

        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
        verify(claimMapper).updateById(captor.capture());
        ClaimEntity capturedClaim = captor.getValue();

        assertEquals(3, capturedClaim.getClaimStatus());
        assertEquals(auditorId, capturedClaim.getAuditorId());
        assertNotNull(capturedClaim.getAuditTime());
    }

    @Test
    @DisplayName("测试 payment 方法 - 理赔打款")
    void testPayment() {
        String claimNo = "CLAIM123";
        when(claimMapper.selectOne(any())).thenReturn(testClaim);
        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);

        claimService.payment(claimNo);

        verify(claimMapper, times(1)).updateById(any(ClaimEntity.class));

        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
        verify(claimMapper).updateById(captor.capture());
        ClaimEntity capturedClaim = captor.getValue();

        assertEquals(4, capturedClaim.getClaimStatus());
        assertNotNull(capturedClaim.getPaymentTime());
    }

    @Test
    @DisplayName("测试 listByUserId 方法 - 查询用户理赔列表")
    void testListByUserId() {
        Long userId = 100L;
        when(claimMapper.selectList(any())).thenReturn(Arrays.asList(testClaim));

        List<ClaimEntity> result = claimService.listByUserId(userId);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        verify(claimMapper, times(1)).selectList(any());
    }

    @Test
    @DisplayName("测试 listByUserId 方法 - 用户无理赔记录")
    void testListByUserIdEmpty() {
        Long userId = 999L;
        when(claimMapper.selectList(any())).thenReturn(Arrays.asList());

        List<ClaimEntity> result = claimService.listByUserId(userId);

        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(claimMapper, times(1)).selectList(any());
    }

    @Test
    @DisplayName("测试边界条件 - claimNo 为 null")
    void testGetClaimWithNullClaimNo() {
        when(claimMapper.selectOne(any())).thenReturn(null);

        ClaimEntity result = claimService.getClaim(null);

        assertNull(result);
    }

    @Test
    @DisplayName("测试边界条件 - claimNo 为空字符串")
    void testGetClaimWithEmptyClaimNo() {
        when(claimMapper.selectOne(any())).thenReturn(null);

        ClaimEntity result = claimService.getClaim("");

        assertNull(result);
    }

    @Test
    @DisplayName("测试事务注解 - rollbackFor 配置")
    void testTransactionalAnnotation() {
        try {
            java.lang.reflect.Method createMethod = ClaimServiceImpl.class.getMethod(
                "createClaim", ClaimEntity.class);
            assertTrue(createMethod.isAnnotationPresent(
                org.springframework.transaction.annotation.Transactional.class));

            org.springframework.transaction.annotation.Transactional transactional =
                createMethod.getAnnotation(
                    org.springframework.transaction.annotation.Transactional.class);

            assertEquals(Exception.class, transactional.rollbackFor()[0]);
        } catch (NoSuchMethodException e) {
            fail("方法不存在：" + e.getMessage());
        }
    }

    @Test
    @DisplayName("测试服务实现类注解")
    void testServiceAnnotations() {
        assertTrue(ClaimServiceImpl.class.isAnnotationPresent(
            org.springframework.stereotype.Service.class));
        assertTrue(ClaimServiceImpl.class.isAnnotationPresent(
            lombok.RequiredArgsConstructor.class));
        assertTrue(ClaimServiceImpl.class.isAnnotationPresent(
            lombok.extern.slf4j.Slf4j.class));
    }

    @Test
    @DisplayName("测试 Mock 对象注入")
    void testMockInjection() {
        assertNotNull(claimService);
        assertNotNull(claimMapper);
    }

    @Test
    @DisplayName("测试 ClaimEntity 实体完整性")
    void testClaimEntity() {
        assertNotNull(testClaim.getId());
        assertNotNull(testClaim.getUserId());
        assertNotNull(testClaim.getClaimType());
        assertNotNull(testClaim.getClaimAmount());
        assertNotNull(testClaim.getRemark());
    }
}
