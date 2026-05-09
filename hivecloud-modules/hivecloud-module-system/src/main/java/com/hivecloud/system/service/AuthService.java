package com.hivecloud.system.service;

import com.hivecloud.system.vo.ChangePasswordRequest;
import com.hivecloud.system.vo.LoginRequest;
import com.hivecloud.system.vo.LoginResponse;
import com.hivecloud.system.vo.ProfileVO;
import com.hivecloud.system.util.CaptchaUtil;

/**
 * 认证服务接口
 * 提供用户登录、注销、个人中心等功能
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface AuthService {

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户注销
     *
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 刷新 Token
     *
     * @param token 原 Token
     * @return 新 Token
     */
    String refreshToken(String token);

    /**
     * 获取个人信息
     *
     * @param userId 用户 ID
     * @return 个人信息
     */
    ProfileVO getProfile(Long userId);

    /**
     * 更新个人信息
     *
     * @param userId 用户 ID
     * @param profileVO 个人信息
     * @return 更新后的个人信息
     */
    ProfileVO updateProfile(Long userId, ProfileVO profileVO);

    /**
     * 修改密码
     *
     * @param userId 用户 ID
     * @param request 修改密码请求
     */
    void changePassword(Long userId, ChangePasswordRequest request);
}
