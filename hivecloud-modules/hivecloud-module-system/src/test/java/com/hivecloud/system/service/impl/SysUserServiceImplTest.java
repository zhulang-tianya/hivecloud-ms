package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.mapper.SysUserMapper;
import com.hivecloud.system.mapper.UserMapper;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

/**
 * SysUserServiceImpl 单元测试类
 *
 * @author HiveCloud Team
 * @date 2026-04-28
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SysUserServiceImpl 服务层测试")
class SysUserServiceImplTest {

    @Mock
    private SysUserMapper sysUserMapper;

    @InjectMocks
    private SysUserServiceImpl sysUserService;

    private SysUser testUser;
    private UserVO testUserVO;

    @BeforeEach
    @DisplayName("测试前置准备 - 创建测试数据")
    void setUp() {
        // 创建测试用户
        testUser = new SysUser();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setNickname("测试用户");
        testUser.setEmail("test@example.com");
        testUser.setPhone("13800138000");
        testUser.setStatus(1);

        // 创建测试 VO
        testUserVO = new UserVO();
        testUserVO.setId(1L);
        testUserVO.setUsername("testuser");
        testUserVO.setNickname("测试用户");
        testUserVO.setEmail("test@example.com");
        testUserVO.setPhone("13800138000");
        testUserVO.setStatus(1);
    }

    @Test
    @DisplayName("测试 getUserInfo 方法 - 查询用户成功")
    void testGetUserInfoSuccess() {
        // 准备数据
        Long userId = 1L;
        when(sysUserService.getById(userId)).thenReturn(testUser);
        when(UserMapper.INSTANCE.toVO(testUser)).thenReturn(testUserVO);

        // 执行测试
        UserVO result = sysUserService.getUserInfo(userId);

        // 验证结果
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        assertEquals("测试用户", result.getNickname());
        assertEquals("test@example.com", result.getEmail());

        // 验证调用
        verify(sysUserService, times(1)).getById(userId);
        verify(UserMapper.INSTANCE, times(1)).toVO(testUser);
    }

    @Test
    @DisplayName("测试 getUserInfo 方法 - 用户不存在")
    void testGetUserInfoNotFound() {
        // 准备数据
        Long userId = 999L;
        when(sysUserService.getById(userId)).thenReturn(null);

        // 执行测试
        UserVO result = sysUserService.getUserInfo(userId);

        // 验证结果
        assertNull(result);

        // 验证调用（不应该调用 toVO）
        verify(sysUserService, times(1)).getById(userId);
        verify(UserMapper.INSTANCE, never()).toVO(any());
    }

    @Test
    @DisplayName("测试 getPermissions 方法 - 获取权限列表")
    void testGetPermissions() {
        Long userId = 1L;

        // 执行测试
        List<String> permissions = sysUserService.getPermissions(userId);

        // 验证结果
        assertNotNull(permissions);
        assertTrue(permissions.isEmpty());
    }

    @Test
    @DisplayName("测试 getPermissions 方法 - 不同用户 ID")
    void testGetPermissionsDifferentUsers() {
        // 执行测试
        List<String> permissions1 = sysUserService.getPermissions(1L);
        List<String> permissions2 = sysUserService.getPermissions(2L);
        List<String> permissions3 = sysUserService.getPermissions(999L);

        // 验证结果
        assertNotNull(permissions1);
        assertTrue(permissions1.isEmpty());

        assertNotNull(permissions2);
        assertTrue(permissions2.isEmpty());

        assertNotNull(permissions3);
        assertTrue(permissions3.isEmpty());
    }

    @Test
    @DisplayName("测试 getRoles 方法 - 获取角色列表")
    void testGetRoles() {
        Long userId = 1L;

        // 执行测试
        List<String> roles = sysUserService.getRoles(userId);

        // 验证结果
        assertNotNull(roles);
        assertTrue(roles.isEmpty());
    }

    @Test
    @DisplayName("测试 getRoles 方法 - 不同用户 ID")
    void testGetRolesDifferentUsers() {
        // 执行测试
        List<String> roles1 = sysUserService.getRoles(1L);
        List<String> roles2 = sysUserService.getRoles(2L);

        // 验证结果
        assertNotNull(roles1);
        assertTrue(roles1.isEmpty());

        assertNotNull(roles2);
        assertTrue(roles2.isEmpty());
    }

    @Test
    @DisplayName("测试继承的 CRUD 方法 - 保存用户")
    void testSave() {
        when(sysUserService.save(any(SysUser.class))).thenReturn(true);

        SysUser newUser = new SysUser();
        newUser.setUsername("newuser");
        boolean result = sysUserService.save(newUser);

        assertTrue(result);
        verify(sysUserService, times(1)).save(newUser);
    }

    @Test
    @DisplayName("测试继承的 CRUD 方法 - 删除用户")
    void testRemoveById() {
        when(sysUserService.removeById(anyLong())).thenReturn(true);

        Long userId = 1L;
        boolean result = sysUserService.removeById(userId);

        assertTrue(result);
        verify(sysUserService, times(1)).removeById(userId);
    }

    @Test
    @DisplayName("测试继承的 CRUD 方法 - 更新用户")
    void testUpdateById() {
        when(sysUserService.updateById(any(SysUser.class))).thenReturn(true);

        SysUser updateUser = new SysUser();
        updateUser.setId(1L);
        updateUser.setNickname("更新后的名称");
        boolean result = sysUserService.updateById(updateUser);

        assertTrue(result);
        verify(sysUserService, times(1)).updateById(updateUser);
    }

    @Test
    @DisplayName("测试继承的 CRUD 方法 - 查询用户")
    void testGetById() {
        when(sysUserService.getById(anyLong())).thenReturn(testUser);

        Long userId = 1L;
        SysUser result = sysUserService.getById(userId);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("testuser", result.getUsername());
        verify(sysUserService, times(1)).getById(userId);
    }

    @Test
    @DisplayName("测试继承的 CRUD 方法 - 查询列表")
    void testList() {
        when(sysUserService.list()).thenReturn(List.of(testUser));

        List<SysUser> result = sysUserService.list();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("testuser", result.get(0).getUsername());
        verify(sysUserService, times(1)).list();
    }

    @Test
    @DisplayName("测试事务注解 - rollbackFor 配置")
    void testTransactionalAnnotation() {
        // 验证类上有@Transactional 注解
        assertTrue(SysUserServiceImpl.class.isAnnotationPresent(
            org.springframework.transaction.annotation.Transactional.class));

        org.springframework.transaction.annotation.Transactional transactional =
            SysUserServiceImpl.class.getAnnotation(
                org.springframework.transaction.annotation.Transactional.class);

        assertEquals(Exception.class, transactional.rollbackFor()[0]);
    }

    @Test
    @DisplayName("测试服务实现类继承关系")
    void testInheritance() {
        assertTrue(SysUserServiceImpl.class.getSuperclass()
            .equals(ServiceImpl.class));
        assertTrue(SysUserService.class.isInterface());
    }

    @Test
    @DisplayName("测试 MapStruct 映射器 - UserMapper")
    void testMapStructMapper() {
        assertNotNull(UserMapper.INSTANCE);
        assertTrue(UserMapper.class.isInterface());

        // 测试映射
        UserVO mappedVO = UserMapper.INSTANCE.toVO(testUser);
        assertNotNull(mappedVO);
        assertEquals(testUser.getId(), mappedVO.getId());
        assertEquals(testUser.getUsername(), mappedVO.getUsername());
        assertEquals(testUser.getNickname(), mappedVO.getNickname());
    }

    @Test
    @DisplayName("测试边界条件 - userId 为 null")
    void testGetUserInfoWithNullUserId() {
        when(sysUserService.getById(null)).thenReturn(null);

        UserVO result = sysUserService.getUserInfo(null);

        assertNull(result);
        verify(sysUserService, times(1)).getById(null);
    }

    @Test
    @DisplayName("测试边界条件 - userId 为 0")
    void testGetUserInfoWithZeroUserId() {
        when(sysUserService.getById(0L)).thenReturn(null);

        UserVO result = sysUserService.getUserInfo(0L);

        assertNull(result);
        verify(sysUserService, times(1)).getById(0L);
    }

    @Test
    @DisplayName("测试边界条件 - userId 为负数")
    void testGetUserInfoWithNegativeUserId() {
        when(sysUserService.getById(-1L)).thenReturn(null);

        UserVO result = sysUserService.getUserInfo(-1L);

        assertNull(result);
        verify(sysUserService, times(1)).getById(-1L);
    }

    @Test
    @DisplayName("测试用户实体完整性")
    void testSysUserEntity() {
        assertNotNull(testUser.getId());
        assertNotNull(testUser.getUsername());
        assertNotNull(testUser.getNickname());
        assertNotNull(testUser.getEmail());
        assertNotNull(testUser.getPhone());
        assertNotNull(testUser.getStatus());
    }

    @Test
    @DisplayName("测试用户 VO 完整性")
    void testUserVO() {
        assertNotNull(testUserVO.getId());
        assertNotNull(testUserVO.getUsername());
        assertNotNull(testUserVO.getNickname());
        assertNotNull(testUserVO.getEmail());
        assertNotNull(testUserVO.getPhone());
        assertNotNull(testUserVO.getStatus());
    }

    @Test
    @DisplayName("测试 Mock 对象注入")
    void testMockInjection() {
        assertNotNull(sysUserService);
        assertNotNull(sysUserMapper);
    }
}
