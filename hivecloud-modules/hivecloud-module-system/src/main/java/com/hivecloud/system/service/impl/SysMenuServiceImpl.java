package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysMenu;
import com.hivecloud.system.mapper.SysMenuMapper;
import com.hivecloud.system.service.SysMenuService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 菜单 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> selectMenuList(SysMenu menu) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(menu.getTenantId() != null, SysMenu::getTenantId, menu.getTenantId())
                .eq(menu.getParentId() != null, SysMenu::getParentId, menu.getParentId())
                .eq(menu.getStatus() != null, SysMenu::getStatus, menu.getStatus())
                .like(menu.getMenuName() != null, SysMenu::getMenuName, menu.getMenuName())
                .orderByAsc(SysMenu::getSort);
        return list(wrapper);
    }

    @Override
    public List<SysMenu> getMenuTree(Long tenantId) {
        LambdaQueryWrapper<SysMenu> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMenu::getTenantId, tenantId)
                .eq(SysMenu::getStatus, 1)
                .orderByAsc(SysMenu::getSort);
        return list(wrapper);
    }

    @Override
    public SysMenu selectMenuById(Long id) {
        return getById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createMenu(SysMenu menu) {
        menu.setCreateTime(LocalDateTime.now());
        menu.setUpdateTime(LocalDateTime.now());
        return save(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateMenu(SysMenu menu) {
        menu.setUpdateTime(LocalDateTime.now());
        return updateById(menu);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteMenuByIds(Long[] menuIds) {
        List<Long> ids = Arrays.asList(menuIds);
        return removeByIds(ids);
    }
}
