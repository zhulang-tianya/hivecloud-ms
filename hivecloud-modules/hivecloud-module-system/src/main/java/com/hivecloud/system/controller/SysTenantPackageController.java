package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysTenantPackage;
import com.hivecloud.system.service.SysTenantPackageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 租户套餐 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/tenant-package")
@RequiredArgsConstructor
@Tag(name = "租户套餐管理", description = "租户套餐信息查询、新增、修改、删除等接口")
public class SysTenantPackageController {

    private final SysTenantPackageService packageService;

    /**
     * 查询套餐列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询套餐列表", description = "支持按套餐名称模糊查询")
    public Result<List<SysTenantPackage>> list(SysTenantPackage pkg) {
        List<SysTenantPackage> list = packageService.selectPackageList(pkg);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询套餐
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询套餐", description = "根据套餐 ID 查询套餐详细信息")
    @Parameter(name = "id", description = "套餐 ID", required = true, example = "1")
    public Result<SysTenantPackage> getInfo(@PathVariable Long id) {
        SysTenantPackage pkg = packageService.selectPackageById(id);
        return Result.success(pkg);
    }

    /**
     * 新增套餐
     */
    @PostMapping
    @Operation(summary = "新增套餐", description = "创建新的租户套餐")
    public Result<Void> add(@RequestBody SysTenantPackage pkg) {
        boolean result = packageService.createPackage(pkg);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改套餐
     */
    @PutMapping
    @Operation(summary = "修改套餐", description = "修改套餐信息")
    public Result<Void> edit(@RequestBody SysTenantPackage pkg) {
        boolean result = packageService.updatePackage(pkg);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除套餐
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除套餐", description = "根据套餐 ID 批量删除套餐")
    @Parameter(name = "ids", description = "套餐 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = packageService.deletePackageByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
