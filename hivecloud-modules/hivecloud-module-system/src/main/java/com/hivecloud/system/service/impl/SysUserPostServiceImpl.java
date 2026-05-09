package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysUserPost;
import com.hivecloud.system.mapper.SysUserPostMapper;
import com.hivecloud.system.service.SysUserPostService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户岗位关联 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysUserPostServiceImpl extends ServiceImpl<SysUserPostMapper, SysUserPost> implements SysUserPostService {

    @Override
    public List<SysUserPost> selectUserPostList(SysUserPost userPost) {
        LambdaQueryWrapper<SysUserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(userPost.getTenantId() != null, SysUserPost::getTenantId, userPost.getTenantId())
                .eq(userPost.getUserId() != null, SysUserPost::getUserId, userPost.getUserId())
                .eq(userPost.getPostId() != null, SysUserPost::getPostId, userPost.getPostId());
        return list(wrapper);
    }

    @Override
    public List<SysUserPost> selectByUserId(Long userId) {
        LambdaQueryWrapper<SysUserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPost::getUserId, userId);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createUserPost(SysUserPost userPost) {
        userPost.setCreateTime(LocalDateTime.now());
        return save(userPost);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUserPostByUserId(Long userId) {
        LambdaQueryWrapper<SysUserPost> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysUserPost::getUserId, userId);
        return remove(wrapper);
    }
}
