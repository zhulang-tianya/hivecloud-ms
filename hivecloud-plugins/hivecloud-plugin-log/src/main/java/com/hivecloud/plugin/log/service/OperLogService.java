package com.hivecloud.plugin.log.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.plugin.log.entity.SysOperLog;

public interface OperLogService extends IService<SysOperLog> {

    IPage<SysOperLog> pageQuery(int pageNum, int pageSize, String title, String operName);
}
