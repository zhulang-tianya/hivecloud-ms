package com.hivecloud.system.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysTenant;
import com.hivecloud.system.service.SysTenantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/tenant")
@RequiredArgsConstructor
@Tag(name = "租户管理", description = "系统租户信息查询、新增、修改、删除等接口")
public class SysTenantController {

    private final SysTenantService tenantService;

    /**
     * 查询租户列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询租户列表", description = "支持按租户名称、编码模糊查询")
    public Result<List<SysTenant>> list(SysTenant tenant) {
        List<SysTenant> list = tenantService.selectTenantList(tenant);
        return Result.success(list);
    }

    /**
     * 分页查询租户列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页查询租户列表", description = "分页查询系统租户信息")
    @Parameter(name = "pageNum", description = "页码", required = true, example = "1")
    @Parameter(name = "pageSize", description = "每页数量", required = true, example = "10")
    public Result<Page<SysTenant>> page(SysTenant tenant,
                                   @RequestParam(defaultValue = "1") Integer pageNum,
                                   @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<SysTenant> page = new Page<>(pageNum, pageSize);
        Page<SysTenant> result = tenantService.page(page);
        return Result.success(result);
    }

    /**
     * 根据 ID 查询租户
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询租户", description = "根据租户 ID 查询租户详细信息")
    @Parameter(name = "id", description = "租户 ID", required = true, example = "1")
    public Result<SysTenant> getInfo(@PathVariable Long id) {
        SysTenant tenant = tenantService.selectTenantById(id);
        return Result.success(tenant);
    }

    /**
     * 根据租户编码查询
     */
    @GetMapping("/code/{tenantCode}")
    @Operation(summary = "根据租户编码查询", description = "根据租户编码查询租户信息")
    @Parameter(name = "tenantCode", description = "租户编码", required = true, example = "default")
    public Result<SysTenant> getInfoByCode(@PathVariable String tenantCode) {
        SysTenant tenant = tenantService.selectTenantByCode(tenantCode);
        return Result.success(tenant);
    }

    /**
     * 新增租户
     */
    @PostMapping
    @Operation(summary = "新增租户", description = "创建新的系统租户")
    public Result<Void> add(@RequestBody SysTenant tenant) {
        boolean result = tenantService.createTenant(tenant);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改租户
     */
    @PutMapping
    @Operation(summary = "修改租户", description = "修改租户信息")
    public Result<Void> edit(@RequestBody SysTenant tenant) {
        boolean result = tenantService.updateTenant(tenant);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除租户
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除租户", description = "根据租户 ID 批量删除租户")
    @Parameter(name = "ids", description = "租户 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = tenantService.deleteTenantByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
