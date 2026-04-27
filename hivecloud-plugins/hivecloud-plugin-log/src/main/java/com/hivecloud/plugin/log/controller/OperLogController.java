package com.hivecloud.plugin.log.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.hivecloud.common.core.result.PageResult;
import com.hivecloud.common.core.result.Result;
import com.hivecloud.plugin.log.entity.SysOperLog;
import com.hivecloud.plugin.log.service.OperLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

/**
 * 操作日志控制器
 * 提供操作日志的查询、删除等接口
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see OperLogService
 */
@Slf4j
@RestController
@RequestMapping("/system/v1/oper-log")
@Validated
@RequiredArgsConstructor
public class OperLogController {

    /**
     * 操作日志服务接口
     */
    private final OperLogService operLogService;

    /**
     * 分页查询操作日志
     *
     * @param pageNum 页码，默认 1
     * @param pageSize 每页大小，默认 10
     * @param title 操作标题（可选）
     * @param operName 操作人员（可选）
     * @return 分页结果
     */
    @GetMapping("/page")
    public Result<PageResult<SysOperLog>> page(
            @RequestParam(defaultValue = "1") @Min(value = 1, message = "页码必须大于 0") int pageNum,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "每页大小必须大于 0") int pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String operName) {
        log.info("分页查询操作日志，pageNum:{}, pageSize:{}, title:{}, operName:{}", pageNum, pageSize, title, operName);
        IPage<SysOperLog> page = operLogService.pageQuery(pageNum, pageSize, title, operName);
        log.info("分页查询操作日志成功，total:{}", page.getTotal());
        return Result.success(PageResult.of(page.getRecords(), page.getTotal(), pageNum, pageSize));
    }

    /**
     * 删除操作日志
     *
     * @param id 日志 ID
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable("id") @Min(value = 1, message = "日志 ID 必须大于 0") Long id) {
        log.info("删除操作日志，id:{}", id);
        operLogService.removeById(id);
        log.info("删除操作日志成功，id:{}", id);
        return Result.success();
    }
}
