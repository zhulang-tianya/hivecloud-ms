package com.hivecloud.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 系统用户 Mapper 接口
 * 继承 MyBatisPlus BaseMapper，获得基础 CRUD 能力
 * 提供用户数据库访问接口
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see BaseMapper
 * @see SysUser
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    /**
     * 根据用户名查询用户
     *
     * @param username 用户名
     * @return 用户信息
     */
    SysUser selectByUsername(@Param("username") String username);
}
