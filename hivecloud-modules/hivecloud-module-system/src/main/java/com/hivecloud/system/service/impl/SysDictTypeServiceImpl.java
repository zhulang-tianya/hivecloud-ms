package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysDictType;
import com.hivecloud.system.mapper.SysDictTypeMapper;
import com.hivecloud.system.service.SysDictTypeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 字典类型 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysDictTypeServiceImpl extends ServiceImpl<SysDictTypeMapper, SysDictType> implements SysDictTypeService {

    @Override
    public List<SysDictType> selectDictTypeList(SysDictType dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictType.getTenantId() != null, SysDictType::getTenantId, dictType.getTenantId())
                .eq(dictType.getStatus() != null, SysDictType::getStatus, dictType.getStatus())
                .like(dictType.getDictName() != null, SysDictType::getDictName, dictType.getDictName())
                .like(dictType.getDictType() != null, SysDictType::getDictType, dictType.getDictType())
                .orderByDesc(SysDictType::getCreateTime);
        return list(wrapper);
    }

    @Override
    public SysDictType selectDictTypeById(Long id) {
        return getById(id);
    }

    @Override
    public SysDictType selectDictTypeByType(String dictType) {
        LambdaQueryWrapper<SysDictType> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictType::getDictType, dictType);
        return getOne(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictType(SysDictType dictType) {
        dictType.setCreateTime(LocalDateTime.now());
        dictType.setUpdateTime(LocalDateTime.now());
        return save(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictType(SysDictType dictType) {
        dictType.setUpdateTime(LocalDateTime.now());
        return updateById(dictType);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictTypeByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return removeByIds(idList);
    }
}
