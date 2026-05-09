package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysMenu;
import com.hivecloud.system.service.SysMenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
@Tag(name = "菜单管理", description = "系统菜单信息查询、新增、修改、删除等接口")
public class SysMenuController {

    private final SysMenuService menuService;

    /**
     * 查询菜单列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询菜单列表", description = "支持按菜单名称模糊查询")
    public Result<List<SysMenu>> list(SysMenu menu) {
        List<SysMenu> list = menuService.selectMenuList(menu);
        return Result.success(list);
    }

    /**
     * 查询菜单树
     */
    @GetMapping("/tree")
    @Operation(summary = "查询菜单树", description = "查询指定租户的菜单树形结构")
    @Parameter(name = "tenantId", description = "租户 ID", required = true, example = "1")
    public Result<List<SysMenu>> tree(@RequestParam Long tenantId) {
        List<SysMenu> list = menuService.getMenuTree(tenantId);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询菜单
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询菜单", description = "根据菜单 ID 查询菜单详细信息")
    @Parameter(name = "id", description = "菜单 ID", required = true, example = "1")
    public Result<SysMenu> getInfo(@PathVariable Long id) {
        SysMenu menu = menuService.selectMenuById(id);
        return Result.success(menu);
    }

    /**
     * 新增菜单
     */
    @PostMapping
    @Operation(summary = "新增菜单", description = "创建新的系统菜单")
    public Result<Void> add(@RequestBody SysMenu menu) {
        boolean result = menuService.createMenu(menu);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改菜单
     */
    @PutMapping
    @Operation(summary = "修改菜单", description = "修改菜单信息")
    public Result<Void> edit(@RequestBody SysMenu menu) {
        boolean result = menuService.updateMenu(menu);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除菜单
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除菜单", description = "根据菜单 ID 批量删除菜单")
    @Parameter(name = "ids", description = "菜单 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = menuService.deleteMenuByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
