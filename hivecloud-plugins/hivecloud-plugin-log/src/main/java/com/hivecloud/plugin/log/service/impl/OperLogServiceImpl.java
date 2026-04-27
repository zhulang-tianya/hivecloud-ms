package com.hivecloud.plugin.log.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.plugin.log.entity.SysOperLog;
import com.hivecloud.plugin.log.mapper.OperLogMapper;
import com.hivecloud.plugin.log.service.OperLogService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service
@Transactional(rollbackFor = Exception.class)
public class OperLogServiceImpl extends ServiceImpl<OperLogMapper, SysOperLog> implements OperLogService {

    @Override
    public IPage<SysOperLog> pageQuery(int pageNum, int pageSize, String title, String operName) {
        LambdaQueryWrapper<SysOperLog> wrapper = new LambdaQueryWrapper<>();
        wrapper.like(StringUtils.hasText(title), SysOperLog::getTitle, title)
               .like(StringUtils.hasText(operName), SysOperLog::getOperName, operName)
               .orderByDesc(SysOperLog::getOperTime);
        return page(new Page<>(pageNum, pageSize), wrapper);
    }
}
