package com.hivecloud.system.mapper;

import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.vo.UserVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * 用户对象映射器
 * 负责 SysUser 实体与 UserVO 视图对象之间的转换
 * 使用 MapStruct 实现高效对象映射，避免手动 setter 赋值
 *
 * @author HiveCloud Team
 * @date 2026-04-27
 * @see SysUser
 * @see UserVO
 */
@Mapper
public interface UserMapper {

    /**
     * 获取单例实例
     */
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    /**
     * Entity 转 VO
     *
     * @param user 用户实体
     * @return 用户视图对象
     */
    UserVO toVO(SysUser user);
}
