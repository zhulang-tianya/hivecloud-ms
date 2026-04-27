package com.hivecloud.plugin.dict.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hivecloud.plugin.cache.core.CacheService;
import com.hivecloud.plugin.dict.entity.DictData;
import com.hivecloud.plugin.dict.entity.DictType;
import com.hivecloud.plugin.dict.mapper.DictDataMapper;
import com.hivecloud.plugin.dict.mapper.DictTypeMapper;
import com.hivecloud.plugin.dict.service.DictService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(rollbackFor = Exception.class)
public class DictServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictService {

    private final DictTypeMapper dictTypeMapper;

    private final CacheService cacheService;

    private static final String DICT_CACHE_PREFIX = "hivecloud:dict:";

    @Override
    public List<DictType> getDictTypes() {
        return dictTypeMapper.selectList(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getStatus, 1)
                .orderByAsc(DictType::getDictCode));
    }

    @Override
    @SuppressWarnings("unchecked")
    public List<DictData> getDictDataByCode(String dictCode) {
        String cacheKey = DICT_CACHE_PREFIX + dictCode;
        List<DictData> data = (List<DictData>) cacheService.get(cacheKey);
        if (data == null) {
            data = baseMapper.selectList(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getDictCode, dictCode)
                    .eq(DictData::getStatus, 1)
                    .orderByAsc(DictData::getSort));
            cacheService.put(cacheKey, data);
        }
        return data;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Map<String, List<DictData>> getAllDictData() {
        Map<String, List<DictData>> result = (Map<String, List<DictData>>) cacheService.get(DICT_CACHE_PREFIX + "all");
        if (result == null) {
            List<DictData> allData = baseMapper.selectList(new LambdaQueryWrapper<DictData>()
                    .eq(DictData::getStatus, 1)
                    .orderByAsc(DictData::getDictCode)
                    .orderByAsc(DictData::getSort));
            result = allData.stream().collect(Collectors.groupingBy(DictData::getDictCode));
            cacheService.put(DICT_CACHE_PREFIX + "all", result);
        }
        return result;
    }

    @Override
    public void refreshCache() {
        cacheService.delete(DICT_CACHE_PREFIX + "all");
        List<DictType> dictTypes = getDictTypes();
        for (DictType dictType : dictTypes) {
            cacheService.delete(DICT_CACHE_PREFIX + dictType.getDictCode());
        }
        getAllDictData();
    }
}
