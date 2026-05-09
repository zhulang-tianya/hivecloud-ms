package com.hivecloud.system.controller;

import com.hivecloud.system.context.TenantContext;
import com.hivecloud.system.service.AuthService;
import com.hivecloud.system.vo.ChangePasswordRequest;
import com.hivecloud.system.vo.ProfileVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 个人中心控制器
 * 提供个人信息查询、修改、密码修改等接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/profile")
@RequiredArgsConstructor
@Tag(name = "个人中心", description = "个人信息管理相关接口")
public class ProfileController {

    private final AuthService authService;

    /**
     * 获取个人信息
     *
     * @return 个人信息
     */
    @GetMapping
    @Operation(summary = "获取个人信息", description = "获取当前登录用户的详细信息（包含角色和权限）")
    public ResponseEntity<ProfileVO> getProfile() {
        Long userId = TenantContext.getUserId();
        ProfileVO profile = authService.getProfile(userId);
        return ResponseEntity.ok(profile);
    }

    /**
     * 更新个人信息
     *
     * @param profileVO 个人信息
     * @return 更新后的个人信息
     */
    @PutMapping
    @Operation(summary = "更新个人信息", description = "修改昵称、手机号、邮箱等信息")
    public ResponseEntity<ProfileVO> updateProfile(@RequestBody ProfileVO profileVO) {
        Long userId = TenantContext.getUserId();
        ProfileVO updated = authService.updateProfile(userId, profileVO);
        return ResponseEntity.ok(updated);
    }

    /**
     * 修改密码
     *
     * @param request 修改密码请求
     */
    @PutMapping("/password")
    @Operation(summary = "修改密码", description = "修改登录密码，密码长度 6-8 位")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        Long userId = TenantContext.getUserId();
        authService.changePassword(userId, request);
        return ResponseEntity.ok().build();
    }

    /**
     * 上传头像
     *
     * @param file 头像文件
     * @return 头像 URL
     */
    @PostMapping("/avatar")
    @Operation(summary = "上传头像", description = "上传用户头像图片")
    public ResponseEntity<Map<String, String>> uploadAvatar(@RequestParam("file") MultipartFile file) {
        // TODO: 实现文件上传逻辑
        // 这里可以集成文件服务，或者本地存储
        // 暂时返回一个示例 URL
        
        Map<String, String> result = new HashMap<>();
        result.put("avatarUrl", "https://example.com/avatar/" + System.currentTimeMillis() + ".jpg");
        
        return ResponseEntity.ok(result);
    }
}
