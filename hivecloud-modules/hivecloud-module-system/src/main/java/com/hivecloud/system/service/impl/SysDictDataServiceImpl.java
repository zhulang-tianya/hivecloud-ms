package com.hivecloud.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.system.entity.SysDictData;
import com.hivecloud.system.mapper.SysDictDataMapper;
import com.hivecloud.system.service.SysDictDataService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * 字典数据 Service 实现类
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
@Service
public class SysDictDataServiceImpl extends ServiceImpl<SysDictDataMapper, SysDictData> implements SysDictDataService {

    @Override
    public List<SysDictData> selectDictDataList(SysDictData dictData) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(dictData.getTenantId() != null, SysDictData::getTenantId, dictData.getTenantId())
                .eq(dictData.getDictTypeId() != null, SysDictData::getDictTypeId, dictData.getDictTypeId())
                .eq(dictData.getStatus() != null, SysDictData::getStatus, dictData.getStatus())
                .like(dictData.getDictLabel() != null, SysDictData::getDictLabel, dictData.getDictLabel())
                .orderByAsc(SysDictData::getSort)
                .orderByDesc(SysDictData::getCreateTime);
        return list(wrapper);
    }

    @Override
    public SysDictData selectDictDataById(Long id) {
        return getById(id);
    }

    @Override
    public List<SysDictData> selectDictDataByTypeId(Long dictTypeId) {
        LambdaQueryWrapper<SysDictData> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDictData::getDictTypeId, dictTypeId)
                .eq(SysDictData::getStatus, 1)
                .orderByAsc(SysDictData::getSort);
        return list(wrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean createDictData(SysDictData dictData) {
        dictData.setCreateTime(LocalDateTime.now());
        dictData.setUpdateTime(LocalDateTime.now());
        return save(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDictData(SysDictData dictData) {
        dictData.setUpdateTime(LocalDateTime.now());
        return updateById(dictData);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteDictDataByIds(Long[] ids) {
        List<Long> idList = Arrays.asList(ids);
        return removeByIds(idList);
    }
}
