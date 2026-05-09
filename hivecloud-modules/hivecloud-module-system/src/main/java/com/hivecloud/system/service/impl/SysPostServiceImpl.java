package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysPost;
import com.hivecloud.system.mapper.SysPostMapper;
import com.hivecloud.system.service.SysPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 岗位 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysPostServiceImpl extends ServiceImpl<SysPostMapper, SysPost> implements SysPostService {

    @Override
    public List<SysPost> selectPostList(SysPost post) {
        LambdaQueryWrapper<SysPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(post.getTenantId() != null, SysPost::getTenantId, post.getTenantId())
                .eq(post.getStatus() != null, SysPost::getStatus, post.getStatus())
                .like(post.getPostName() != null, SysPost::getPostName, post.getPostName())
                .orderByAsc(SysPost::getSort)
                .orderByDesc(SysPost::getCreateTime);
        return list(wrapper);
    }

    @Override
    public SysPost selectPostById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createPost(SysPost post) {
        post.setCreateTime(LocalDateTime.now());
        post.setUpdateTime(LocalDateTime.now());
        return save(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updatePost(SysPost post) {
        post.setUpdateTime(LocalDateTime.now());
        return updateById(post);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deletePostByIds(Long[] postIds) {
        List<Long> ids = Arrays.asList(postIds);
        return removeByIds(ids);
    }
}
