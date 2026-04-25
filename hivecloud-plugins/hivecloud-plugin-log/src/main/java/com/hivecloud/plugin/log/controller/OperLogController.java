package com.hivecloud.plugin.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hivecloud.common.core.result.PageResult;
import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.log.entity.SysOperLog;
import com.hivecloud.plugin.log.service.OperLogService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/system/v1/oper-log")
@RequiredArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @GetMapping("/page")
    public Result<PageResult<SysOperLog>> page(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName) {
        IPage<SysOperLog> page = operLogService.pageQuery(pageNum, pageSize, title, operName);
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        operLogService.removeById(id);
        return Result.success();
    }
}
