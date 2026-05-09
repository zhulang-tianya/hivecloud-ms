package com.hivecloud.system.controller;

import com.hivecloud.common.core.result.Result;
import com.hivecloud.system.entity.SysPost;
import com.hivecloud.system.service.SysPostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 岗位 Controller
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Slf4j
@RestController
@RequestMapping("/system/post")
@RequiredArgsConstructor
@Tag(name = "岗位管理", description = "系统岗位信息查询、新增、修改、删除等接口")
public class SysPostController {

    private final SysPostService postService;

    /**
     * 查询岗位列表
     */
    @GetMapping("/list")
    @Operation(summary = "查询岗位列表", description = "支持按岗位名称、编码模糊查询")
    public Result<List<SysPost>> list(SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        return Result.success(list);
    }

    /**
     * 根据 ID 查询岗位
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询岗位", description = "根据岗位 ID 查询岗位详细信息")
    @Parameter(name = "id", description = "岗位 ID", required = true, example = "1")
    public Result<SysPost> getInfo(@PathVariable Long id) {
        SysPost post = postService.selectPostById(id);
        return Result.success(post);
    }

    /**
     * 新增岗位
     */
    @PostMapping
    @Operation(summary = "新增岗位", description = "创建新的系统岗位")
    public Result<Void> add(@RequestBody SysPost post) {
        boolean result = postService.createPost(post);
        return result ? Result.success() : Result.error("新增失败");
    }

    /**
     * 修改岗位
     */
    @PutMapping
    @Operation(summary = "修改岗位", description = "修改岗位信息")
    public Result<Void> edit(@RequestBody SysPost post) {
        boolean result = postService.updatePost(post);
        return result ? Result.success() : Result.error("修改失败");
    }

    /**
     * 删除岗位
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除岗位", description = "根据岗位 ID 批量删除岗位")
    @Parameter(name = "ids", description = "岗位 ID 数组", required = true)
    public Result<Void> remove(@PathVariable Long[] ids) {
        boolean result = postService.deletePostByIds(ids);
        return result ? Result.success() : Result.error("删除失败");
    }
}
