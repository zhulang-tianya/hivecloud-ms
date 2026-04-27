package com.hivecloud.plugin.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.plugin.log.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志 Mapper 接口
 * 继承 MyBatisPlus BaseMapper，获得基础 CRUD 能力
 * 提供操作日志数据库访问接口
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see BaseMapper
 * @see SysOperLog
 */
@Mapper
public interface OperLogMapper extends BaseMapper<SysOperLog> {
}
