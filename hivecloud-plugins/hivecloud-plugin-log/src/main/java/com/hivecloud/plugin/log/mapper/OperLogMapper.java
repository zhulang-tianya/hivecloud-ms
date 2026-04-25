package com.hivecloud.plugin.log.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.plugin.log.entity.SysOperLog;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OperLogMapper extends BaseMapper<SysOperLog> {
}
