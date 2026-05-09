package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysPost;

import java.util.List;

/**
 * 岗位 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysPostService extends IService<SysPost> {

    /**
     * 查询岗位列表
     *
     * @param post 岗位查询条件
     * @return 岗位列表
     */
    List<SysPost> selectPostList(SysPost post);

    /**
     * 根据 ID 查询岗位
     *
     * @param id 岗位 ID
     * @return 岗位信息
     */
    SysPost selectPostById(Long id);

    /**
     * 创建岗位
     *
     * @param post 岗位信息
     * @return 是否成功
     */
    boolean createPost(SysPost post);

    /**
     * 更新岗位
     *
     * @param post 岗位信息
     * @return 是否成功
     */
    boolean updatePost(SysPost post);

    /**
     * 删除岗位
     *
     * @param postIds 岗位 ID 数组
     * @return 是否成功
     */
    boolean deletePostByIds(Long[] postIds);
}
