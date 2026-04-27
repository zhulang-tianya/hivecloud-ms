package com.hivecloud.plugin.dict.controller;

import com.hivecloud.common.core.annotation.Idempotent;
import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.dict.entity.DictData;
import com.hivecloud.plugin.dict.entity.DictType;
import com.hivecloud.plugin.dict.service.DictService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;
import java.util.Map;

/**
 * 字典管理控制器
 * 提供字典类型、字典数据的查询接口
 * 支持刷新字典缓存功能
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see DictService
 * @see Result
 */
@Slf4j
@RestController
@RequestMapping("/system/v1/dict")
@Validated
@RequiredArgsConstructor
public class DictController {

    /**
     * 字典服务接口
     */
    private final DictService dictService;

    /**
     * 获取字典类型列表
     *
     * @return 字典类型列表
     */
    @GetMapping("/types")
    public Result<List<DictType>> getDictTypes() {
        return Result.success(dictService.getDictTypes());
    }

    /**
     * 根据字典编码获取字典数据
     *
     * @param dictCode 字典编码，不能为空
     * @return 字典数据列表
     */
    @GetMapping("/data/{dictCode}")
    public Result<List<DictData>> getDictData(@PathVariable("dictCode") @NotBlank(message = "字典编码不能为空") String dictCode) {
        log.info("查询字典数据，dictCode:{}", dictCode);
        return Result.success(dictService.getDictDataByCode(dictCode));
    }

    /**
     * 获取所有字典数据
     *
     * @return 所有字典数据（按字典编码分组）
     */
    @GetMapping("/data/all")
    public Result<Map<String, List<DictData>>> getAllDictData() {
        log.info("查询所有字典数据");
        return Result.success(dictService.getAllDictData());
    }

    /**
     * 刷新字典缓存
     * 幂等接口，防止重复刷新导致缓存抖动
     *
     * @return 操作结果
     */
    @Idempotent(key = "'dict:refresh:' + T(java.lang.System).currentTimeMillis() / 60000", expire = 60)
    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        log.info("刷新字典缓存");
        dictService.refreshCache();
        return Result.success();
    }
}
