package com.hivecloud.module.claim.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hivecloud.module.claim.entity.ClaimEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 理赔申请 Mapper 接口
 * 基于 MyBatis-Plus 实现，提供 CRUD 操作
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see ClaimEntity
 */
@Mapper
public interface ClaimMapper extends BaseMapper<ClaimEntity> {

}
