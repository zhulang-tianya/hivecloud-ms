package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysDept;
import com.hivecloud.system.service.SysDeptService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
@Tag(name = "部门管理", description = "系统部门信息查询、新增、修改、删除等接口")
public class SysDeptController {

    private final SysDeptService deptService;

    /**
     * 查询部门列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询部门列表", description = "支持按部门名称模糊查询")
    public Result<List<SysDept>> list(SysDept dept) {
        List<SysDept> list = deptService.selectDeptList(dept);
        return Result.success(list);
    }

    /**
     * 查询部门树
     */
    @GetMapping("/tree")
    @Operation(summary = "查询部门树", description = "查询指定租户的部门树形结构")
    @Parameter(name = "tenantId", description = "租户 ID", required = true, example = "1")
    public Result<List<SysDept>> tree(@RequestParam Long tenantId) {
        List<SysDept> list = deptService.getDeptTree(tenantId);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询部门
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询部门", description = "根据部门 ID 查询部门详细信息")
    @Parameter(name = "id", description = "部门 ID", required = true, example = "1")
    public Result<SysDept> getInfo(@PathVariable Long id) {
        SysDept dept = deptService.selectDeptById(id);
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @PostMapping
    @Operation(summary = "新增部门", description = "创建新的系统部门")
    public Result<Void> add(@RequestBody SysDept dept) {
        boolean result = deptService.createDept(dept);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改部门
     */
    @PutMapping
    @Operation(summary = "修改部门", description = "修改部门信息")
    public Result<Void> edit(@RequestBody SysDept dept) {
        boolean result = deptService.updateDept(dept);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除部门
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除部门", description = "根据部门 ID 批量删除部门")
    @Parameter(name = "ids", description = "部门 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = deptService.deleteDeptByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
