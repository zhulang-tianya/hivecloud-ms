package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysUserPost;

import java.util.List;

/**
 * 用户岗位关联 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysUserPostService extends IService<SysUserPost> {

    /**
     * 查询用户岗位列表
     *
     * @param userPost 查询条件
     * @return 用户岗位列表
     */
    List<SysUserPost> selectUserPostList(SysUserPost userPost);

    /**
     * 根据用户 ID 查询岗位
     *
     * @param userId 用户 ID
     * @return 用户岗位列表
     */
    List<SysUserPost> selectByUserId(Long userId);

    /**
     * 创建用户岗位关联
     *
     * @param userPost 用户岗位信息
     * @return 是否成功
     */
    boolean createUserPost(SysUserPost userPost);

    /**
     * 删除用户岗位关联
     *
     * @param userId 用户 ID
     * @return 是否成功
     */
    boolean deleteUserPostByUserId(Long userId);
}
