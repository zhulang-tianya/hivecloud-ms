package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysDictType;
import com.hivecloud.system.service.SysDictTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/dict-type")
@RequiredArgsConstructor
@Tag(name = "字典类型管理", description = "字典类型信息查询、新增、修改、删除等接口")
public class SysDictTypeController {

    private final SysDictTypeService dictTypeService;

    /**
     * 查询字典类型列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典类型列表", description = "支持按字典名称、编码模糊查询")
    public Result<List<SysDictType>> list(SysDictType dictType) {
        List<SysDictType> list = dictTypeService.selectDictTypeList(dictType);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询字典类型
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询字典类型", description = "根据字典类型 ID 查询详细信息")
    @Parameter(name = "id", description = "字典类型 ID", required = true, example = "1")
    public Result<SysDictType> getInfo(@PathVariable Long id) {
        SysDictType dictType = dictTypeService.selectDictTypeById(id);
        return Result.success(dictType);
    }

    /**
     * 根据字典类型标识查询
     */
    @GetMapping("/type/{dictType}")
    @Operation(summary = "根据字典类型标识查询", description = "根据字典类型标识查询字典类型信息")
    @Parameter(name = "dictType", description = "字典类型标识", required = true, example = "sys_user_sex")
    public Result<SysDictType> getInfoByType(@PathVariable String dictType) {
        SysDictType dictTypeInfo = dictTypeService.selectDictTypeByType(dictType);
        return Result.success(dictTypeInfo);
    }

    /**
     * 新增字典类型
     */
    @PostMapping
    @Operation(summary = "新增字典类型", description = "创建新的字典类型")
    public Result<Void> add(@RequestBody SysDictType dictType) {
        boolean result = dictTypeService.createDictType(dictType);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改字典类型
     */
    @PutMapping
    @Operation(summary = "修改字典类型", description = "修改字典类型信息")
    public Result<Void> edit(@RequestBody SysDictType dictType) {
        boolean result = dictTypeService.updateDictType(dictType);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典类型", description = "根据字典类型 ID 批量删除")
    @Parameter(name = "ids", description = "字典类型 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = dictTypeService.deleteDictTypeByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
