package com.hivecloud.plugin.dict.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.dict.entity.DictData;
import com.hivecloud.plugin.dict.entity.DictType;
import com.hivecloud.plugin.dict.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/system/v1/dict")
@RequiredArgsConstructor
public class DictController {

    private final DictService dictService;

    @GetMapping("/types")
    public Result<List<DictType>> getDictTypes() {
        return Result.success(dictService.getDictTypes());
    }

    @GetMapping("/data/{dictCode}")
    public Result<List<DictData>> getDictData(@PathVariable String dictCode) {
        return Result.success(dictService.getDictDataByCode(dictCode));
    }

    @GetMapping("/data/all")
    public Result<Map<String, List<DictData>>> getAllDictData() {
        return Result.success(dictService.getAllDictData());
    }

    @PostMapping("/refresh")
    public Result<Void> refreshCache() {
        dictService.refreshCache();
        return Result.success();
    }
}
