package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysUser;
import com.hivecloud.system.mapper.SysUserMapper;
import com.hivecloud.system.service.SysUserService;
import com.hivecloud.system.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public UserVO getUserInfo(Long userId) {
        SysUser user = getById(userId);
        if (user == null) {
            return null;
        }
        UserVO vo = new UserVO();
        vo.setId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setNickname(user.getNickname());
        vo.setEmail(user.getEmail());
        vo.setPhone(user.getPhone());
        vo.setAvatar(user.getAvatar());
        vo.setGender(user.getGender());
        vo.setStatus(user.getStatus());
        vo.setCreateTime(user.getCreateTime());
        return vo;
    }

    @Override
    public List<String> getPermissions(Long userId) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoles(Long userId) {
        return Collections.emptyList();
    }
}
