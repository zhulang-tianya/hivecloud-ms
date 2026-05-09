package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysRole;
import com.hivecloud.system.service.SysRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
@Tag(name = "角色管理", description = "系统角色信息查询、新增、修改、删除等接口")
public class SysRoleController {

    private final SysRoleService roleService;

    /**
     * 查询角色列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询角色列表", description = "支持按角色名称、编码模糊查询")
    public Result<List<SysRole>> list(SysRole role) {
        List<SysRole> list = roleService.selectRoleList(role);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询角色
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询角色", description = "根据角色 ID 查询角色详细信息")
    @Parameter(name = "id", description = "角色 ID", required = true, example = "1")
    public Result<SysRole> getInfo(@PathVariable Long id) {
        SysRole role = roleService.selectRoleById(id);
        return Result.success(role);
    }

    /**
     * 根据角色编码查询
     */
    @GetMapping("/code/{roleCode}")
    @Operation(summary = "根据角色编码查询", description = "根据角色编码查询角色信息")
    @Parameter(name = "roleCode", description = "角色编码", required = true, example = "admin")
    public Result<SysRole> getInfoByCode(@PathVariable String roleCode) {
        SysRole role = roleService.selectRoleByCode(roleCode);
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @PostMapping
    @Operation(summary = "新增角色", description = "创建新的系统角色")
    public Result<Void> add(@RequestBody SysRole role) {
        boolean result = roleService.createRole(role);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改角色
     */
    @PutMapping
    @Operation(summary = "修改角色", description = "修改角色信息")
    public Result<Void> edit(@RequestBody SysRole role) {
        boolean result = roleService.updateRole(role);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除角色
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除角色", description = "根据角色 ID 批量删除角色")
    @Parameter(name = "ids", description = "角色 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = roleService.deleteRoleByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
