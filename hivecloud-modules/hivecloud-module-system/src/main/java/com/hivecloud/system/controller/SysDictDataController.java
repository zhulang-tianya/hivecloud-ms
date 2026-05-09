package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysDictData;
import com.hivecloud.system.service.SysDictDataService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典数据 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/dict-data")
@RequiredArgsConstructor
@Tag(name = "字典数据管理", description = "字典数据信息查询、新增、修改、删除等接口")
public class SysDictDataController {

    private final SysDictDataService dictDataService;

    /**
     * 查询字典数据列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询字典数据列表", description = "支持按字典类型、数据标签模糊查询")
    public Result<List<SysDictData>> list(SysDictData dictData) {
        List<SysDictData> list = dictDataService.selectDictDataList(dictData);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询字典数据
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询字典数据", description = "根据字典数据 ID 查询详细信息")
    @Parameter(name = "id", description = "字典数据 ID", required = true, example = "1")
    public Result<SysDictData> getInfo(@PathVariable Long id) {
        SysDictData dictData = dictDataService.selectDictDataById(id);
        return Result.success(dictData);
    }

    /**
     * 根据字典类型查询数据
     */
    @GetMapping("/type/{dictTypeId}")
    @Operation(summary = "根据字典类型查询数据", description = "根据字典类型 ID 查询所有字典数据")
    @Parameter(name = "dictTypeId", description = "字典类型 ID", required = true, example = "1")
    public Result<List<SysDictData>> listByTypeId(@PathVariable Long dictTypeId) {
        List<SysDictData> list = dictDataService.selectDictDataByTypeId(dictTypeId);
        return Result.success(list);
    }

    /**
     * 新增字典数据
     */
    @PostMapping
    @Operation(summary = "新增字典数据", description = "创建新的字典数据")
    public Result<Void> add(@RequestBody SysDictData dictData) {
        boolean result = dictDataService.createDictData(dictData);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改字典数据
     */
    @PutMapping
    @Operation(summary = "修改字典数据", description = "修改字典数据信息")
    public Result<Void> edit(@RequestBody SysDictData dictData) {
        boolean result = dictDataService.updateDictData(dictData);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除字典数据", description = "根据字典数据 ID 批量删除")
    @Parameter(name = "ids", description = "字典数据 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = dictDataService.deleteDictDataByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
