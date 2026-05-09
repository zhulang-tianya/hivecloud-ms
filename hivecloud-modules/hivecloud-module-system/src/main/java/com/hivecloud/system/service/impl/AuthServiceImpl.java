package com.hivecloud.system.service.impl;

import com.hivecloud.system.entity.SysTenant;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.mapper.SysTenantMapper;
import com.hivecloud.system.mapper.SysUserMapper;
import com.hivecloud.system.service.AuthService;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.util.CaptchaUtil;
import com.hivecloud.system.util.JwtUtil;
import com.hivecloud.system.vo.ChangePasswordRequest;
import com.hivecloud.system.vo.LoginRequest;
import com.hivecloud.system.vo.LoginResponse;
import com.hivecloud.system.vo.ProfileVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 认证服务实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final SysUserMapper sysUserMapper;
    private final SysTenantMapper sysTenantMapper;
    private final SysUserService sysUserService;
    private final JwtUtil jwtUtil;
    private final CaptchaUtil captchaUtil;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LoginResponse login(LoginRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        String captcha = request.getCaptcha();
        String captchaKey = request.getCaptchaKey();

        // 1. 验证验证码
        if (!captchaUtil.validateCaptcha(captchaKey, captcha)) {
            throw new RuntimeException("验证码错误");
        }

        // 2. 查询用户
        SysUser user = sysUserMapper.selectByUsername(username);
        if (user == null) {
            log.warn("用户不存在：username={}", username);
            throw new RuntimeException("用户名或密码错误");
        }

        // 3. 验证密码
        if (!passwordEncoder.matches(password, user.getPassword())) {
            log.warn("密码错误：username={}", username);
            throw new RuntimeException("用户名或密码错误");
        }

        // 4. 检查用户状态
        if (user.getStatus() == 0) {
            log.warn("用户已被禁用：username={}", username);
            throw new RuntimeException("账号已被禁用");
        }

        // 5. 检查租户状态
        SysTenant tenant = sysTenantMapper.selectById(user.getTenantId());
        if (tenant == null) {
            log.warn("租户不存在：tenantId={}", user.getTenantId());
            throw new RuntimeException("租户不存在");
        }
        
        if (tenant.getStatus() == 0) {
            log.warn("租户已被禁用：tenantId={}", user.getTenantId());
            throw new RuntimeException("租户已被禁用");
        }
        
        if (tenant.getExpireTime() != null && tenant.getExpireTime().isBefore(LocalDateTime.now())) {
            log.warn("租户已过期：tenantId={}, expireTime={}", user.getTenantId(), tenant.getExpireTime());
            throw new RuntimeException("租户已过期");
        }

        // 6. 检查密码是否过期
        if (user.getPasswordExpireTime() != null && 
            user.getPasswordExpireTime().isBefore(LocalDateTime.now())) {
            log.warn("密码已过期：username={}, expireTime={}", username, user.getPasswordExpireTime());
            // 密码过期时仍然允许登录，但前端需要提示用户修改密码
        }

        // 7. 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getTenantId());
        long expiresIn = jwtUtil.getRemainingTime(token);

        // 8. 获取用户角色和权限
        var userInfo = LoginResponse.UserInfoVO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .roles(sysUserService.getRoles(user.getId()))
                .permissions(sysUserService.getPermissions(user.getId()))
                .build();

        log.info("用户登录成功：username={}, userId={}", username, user.getId());

        return LoginResponse.builder()
                .token(token)
                .expiresIn(expiresIn)
                .userInfo(userInfo)
                .build();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void logout(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            return;
        }

        String accessToken = token.substring(7);
        
        try {
            // 获取 Token 剩余有效期
            long remainingTime = jwtUtil.getRemainingTime(accessToken);
            
            if (remainingTime > 0) {
                // 将 Token 加入黑名单（Redis 缓存，剩余有效期后自动过期）
                String blacklistKey = "token:blacklist:" + accessToken;
                redisTemplate.opsForValue().set(blacklistKey, "blacklisted", remainingTime, TimeUnit.SECONDS);
                log.debug("Token 已加入黑名单：剩余时间={}秒", remainingTime);
            }
        } catch (Exception e) {
            log.warn("注销失败：{}", e.getMessage());
        }
    }

    @Override
    public String refreshToken(String token) {
        // 验证 Token 是否有效
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token 无效或已过期");
        }

        // 检查是否在黑名单中
        String blacklistKey = "token:blacklist:" + token;
        if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
            throw new RuntimeException("Token 已注销");
        }

        // 判断是否需要刷新
        if (!jwtUtil.shouldRefresh(token)) {
            return token; // 不需要刷新，返回原 Token
        }

        // 刷新 Token
        String newToken = jwtUtil.refreshToken(token);
        log.debug("Token 已刷新：userId={}", jwtUtil.getUserId(token));

        return newToken;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileVO getProfile(Long userId) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        ProfileVO profileVO = new ProfileVO();
        profileVO.setId(user.getId());
        profileVO.setUsername(user.getUsername());
        profileVO.setNickname(user.getNickname());
        profileVO.setPhone(user.getPhone());
        profileVO.setEmail(user.getEmail());
        profileVO.setAvatar(user.getAvatar());
        profileVO.setGender(user.getGender());
        profileVO.setCreateTime(user.getCreateTime());
        profileVO.setPasswordExpireTime(user.getPasswordExpireTime());

        // 获取部门信息
        if (user.getDeptId() != null) {
            // 这里可以通过 deptMapper 查询部门详情
            // 暂时返回 null，后续可以完善
        }

        // 获取角色和权限
        profileVO.setRoles(sysUserService.getRoles(userId));
        profileVO.setPermissions(sysUserService.getPermissions(userId));

        return profileVO;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ProfileVO updateProfile(Long userId, ProfileVO profileVO) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 更新允许修改的字段
        if (profileVO.getNickname() != null) {
            user.setNickname(profileVO.getNickname());
        }
        if (profileVO.getPhone() != null) {
            user.setPhone(profileVO.getPhone());
        }
        if (profileVO.getEmail() != null) {
            user.setEmail(profileVO.getEmail());
        }
        if (profileVO.getGender() != null) {
            user.setGender(profileVO.getGender());
        }

        sysUserMapper.updateById(user);
        log.info("用户信息更新成功：userId={}", userId);

        return getProfile(userId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changePassword(Long userId, ChangePasswordRequest request) {
        SysUser user = sysUserMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        // 验证旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("旧密码错误");
        }

        // 更新密码
        String newPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(newPassword);
        
        // 设置密码过期时间（3 个月后）
        user.setPasswordExpireTime(LocalDateTime.now().plusMonths(3));
        
        sysUserMapper.updateById(user);
        log.info("密码修改成功：userId={}", userId);
    }
}
