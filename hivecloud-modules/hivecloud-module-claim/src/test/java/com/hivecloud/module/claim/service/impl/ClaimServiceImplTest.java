//package com.hivecloud.module.claim.service.impl;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import com.hivecloud.module.claim.entity.ClaimEntity;
//import com.hivecloud.module.claim.mapper.ClaimMapper;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.ArgumentCaptor;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.math.BigDecimal;
//import java.time.LocalDateTime;
//import java.util.ArrayList;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.*;
//
///**
// * 理赔服务实现单元测试类
// * 测试 ClaimServiceImpl 的核心业务逻辑
// *
// * @author HiveCloud Team
// * @date 2026-04-27
// * @see ClaimServiceImpl
// * @see ClaimEntity
// */
//@ExtendWith(MockitoExtension.class)
//@DisplayName("ClaimServiceImpl 单元测试")
//class ClaimServiceImplTest {
//
//    @Mock
//    private ClaimMapper claimMapper;
//
//    @InjectMocks
//    private ClaimServiceImpl claimService;
//
//    private ClaimEntity testClaim;
//
//    @BeforeEach
//    void setUp() {
//        testClaim = new ClaimEntity();
//        testClaim.setUserId(1000000001L);
//        testClaim.setClaimType("MEDICAL");
//        testClaim.setClaimAmount(new BigDecimal("5000.00"));
//        testClaim.setClaimStatus(0);
//        testClaim.setApplyTime(LocalDateTime.now());
//        testClaim.setRemark("测试理赔申请");
//    }
//
//    @Test
//    @DisplayName("创建理赔申请 - 成功")
//    void testCreateClaim_Success() {
//        // Arrange
//        when(claimMapper.insert(any(ClaimEntity.class))).thenReturn(1);
//
//        // Act
//        String claimNo = claimService.createClaim(testClaim);
//
//        // Assert
//        assertNotNull(claimNo);
//        assertTrue(claimNo.startsWith("CLAIM"));
//        verify(claimMapper, times(1)).insert(any(ClaimEntity.class));
//    }
//
//    @Test
//    @DisplayName("获取理赔申请 - 成功")
//    void testGetClaim_Success() {
//        // Arrange
//        String claimNo = "CLAIM1234567890";
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testClaim);
//
//        // Act
//        ClaimEntity result = claimService.getClaim(claimNo);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1000000001L, result.getUserId());
//        assertEquals("MEDICAL", result.getClaimType());
//        assertEquals(new BigDecimal("5000.00"), result.getClaimAmount());
//        verify(claimMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
//    }
//
//    @Test
//    @DisplayName("获取理赔申请 - 未找到")
//    void testGetClaim_NotFound() {
//        // Arrange
//        String claimNo = "CLAIM_NOT_EXIST";
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
//
//        // Act
//        ClaimEntity result = claimService.getClaim(claimNo);
//
//        // Assert
//        assertNull(result);
//        verify(claimMapper, times(1)).selectOne(any(LambdaQueryWrapper.class));
//    }
//
//    @Test
//    @DisplayName("审核理赔申请 - 通过")
//    void testAuditClaim_Approve() {
//        // Arrange
//        String claimNo = "CLAIM1234567890";
//        Long auditorId = 2000000001L;
//        boolean approved = true;
//        String opinion = "审核通过，符合理赔条件";
//
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testClaim);
//        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);
//
//        // Act
//        boolean result = claimService.auditClaim(claimNo, auditorId, approved, opinion);
//
//        // Assert
//        assertTrue(result);
//        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
//        verify(claimMapper).updateById(captor.capture());
//
//        ClaimEntity updatedClaim = captor.getValue();
//        assertEquals(2, updatedClaim.getClaimStatus());
//        assertEquals(auditorId, updatedClaim.getAuditorId());
//        assertEquals(opinion, updatedClaim.getAuditOpinion());
//        assertNotNull(updatedClaim.getAuditTime());
//    }
//
//    @Test
//    @DisplayName("审核理赔申请 - 拒绝")
//    void testAuditClaim_Reject() {
//        // Arrange
//        String claimNo = "CLAIM1234567890";
//        Long auditorId = 2000000001L;
//        boolean approved = false;
//        String opinion = "审核拒绝，不符合理赔条件";
//
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testClaim);
//        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);
//
//        // Act
//        boolean result = claimService.auditClaim(claimNo, auditorId, approved, opinion);
//
//        // Assert
//        assertTrue(result);
//        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
//        verify(claimMapper).updateById(captor.capture());
//
//        ClaimEntity updatedClaim = captor.getValue();
//        assertEquals(3, updatedClaim.getClaimStatus());
//        assertEquals(auditorId, updatedClaim.getAuditorId());
//        assertEquals(opinion, updatedClaim.getAuditOpinion());
//        assertNotNull(updatedClaim.getAuditTime());
//    }
//
//    @Test
//    @DisplayName("审核理赔申请 - 申请不存在")
//    void testAuditClaim_NotFound() {
//        // Arrange
//        String claimNo = "CLAIM_NOT_EXIST";
//        Long auditorId = 2000000001L;
//        boolean approved = true;
//        String opinion = "审核意见";
//
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
//
//        // Act
//        boolean result = claimService.auditClaim(claimNo, auditorId, approved, opinion);
//
//        // Assert
//        assertFalse(result);
//        verify(claimMapper, never()).updateById(any(ClaimEntity.class));
//    }
//
//    @Test
//    @DisplayName("理赔打款 - 成功")
//    void testPayment_Success() {
//        // Arrange
//        String claimNo = "CLAIM1234567890";
//        testClaim.setClaimStatus(2); // 审核通过
//
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testClaim);
//        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);
//
//        // Act
//        boolean result = claimService.payment(claimNo);
//
//        // Assert
//        assertTrue(result);
//        ArgumentCaptor<ClaimEntity> captor = ArgumentCaptor.forClass(ClaimEntity.class);
//        verify(claimMapper).updateById(captor.capture());
//
//        ClaimEntity updatedClaim = captor.getValue();
//        assertEquals(4, updatedClaim.getClaimStatus());
//        assertNotNull(updatedClaim.getPaymentTime());
//    }
//
//    @Test
//    @DisplayName("理赔打款 - 状态不正确")
//    void testPayment_InvalidStatus() {
//        // Arrange
//        String claimNo = "CLAIM1234567890";
//        testClaim.setClaimStatus(0); // 待审核，状态不正确
//
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(testClaim);
//
//        // Act
//        boolean result = claimService.payment(claimNo);
//
//        // Assert
//        assertFalse(result);
//        verify(claimMapper, never()).updateById(any(ClaimEntity.class));
//    }
//
//    @Test
//    @DisplayName("理赔打款 - 申请不存在")
//    void testPayment_NotFound() {
//        // Arrange
//        String claimNo = "CLAIM_NOT_EXIST";
//
//        when(claimMapper.selectOne(any(LambdaQueryWrapper.class))).thenReturn(null);
//
//        // Act
//        boolean result = claimService.payment(claimNo);
//
//        // Assert
//        assertFalse(result);
//        verify(claimMapper, never()).updateById(any(ClaimEntity.class));
//    }
//
//    @Test
//    @DisplayName("根据用户 ID 列表查询 - 成功")
//    void testListByUserId_Success() {
//        // Arrange
//        Long userId = 1000000001L;
//        List<ClaimEntity> mockClaims = new ArrayList<>();
//        mockClaims.add(testClaim);
//
//        when(claimMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(mockClaims);
//
//        // Act
//        List<ClaimEntity> result = claimService.listByUserId(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertEquals(1, result.size());
//        assertEquals(userId, result.get(0).getUserId());
//        verify(claimMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
//    }
//
//    @Test
//    @DisplayName("根据用户 ID 列表查询 - 空结果")
//    void testListByUserId_Empty() {
//        // Arrange
//        Long userId = 1000000001L;
//        when(claimMapper.selectList(any(LambdaQueryWrapper.class))).thenReturn(new ArrayList<>());
//
//        // Act
//        List<ClaimEntity> result = claimService.listByUserId(userId);
//
//        // Assert
//        assertNotNull(result);
//        assertTrue(result.isEmpty());
//        verify(claimMapper, times(1)).selectList(any(LambdaQueryWrapper.class));
//    }
//
//    @Test
//    @DisplayName("更新理赔申请 - 成功")
//    void testUpdateClaim_Success() {
//        // Arrange
//        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(1);
//
//        // Act
//        boolean result = claimService.updateClaim(testClaim);
//
//        // Assert
//        assertTrue(result);
//        verify(claimMapper, times(1)).updateById(any(ClaimEntity.class));
//    }
//
//    @Test
//    @DisplayName("更新理赔申请 - 失败")
//    void testUpdateClaim_Failure() {
//        // Arrange
//        when(claimMapper.updateById(any(ClaimEntity.class))).thenReturn(0);
//
//        // Act
//        boolean result = claimService.updateClaim(testClaim);
//
//        // Assert
//        assertFalse(result);
//        verify(claimMapper, times(1)).updateById(any(ClaimEntity.class));
//    }
//}
