package com.hivecloud.plugin.dict.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hivecloud.plugin.cache.core.CacheService;
import com.hivecloud.plugin.dict.entity.SysDict;
import com.hivecloud.plugin.dict.mapper.SysDictMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
public class DictService {

    private static final String DICT_CACHE_PREFIX = "hivecloud:dict:";
    private static final long CACHE_TTL_MINUTES = 30;

    private final SysDictMapper dictMapper;
    private final CacheService cacheService;

    public String getDictLabel(String dictCode, String dictValue) {
        String cacheKey = DICT_CACHE_PREFIX + dictCode + ":" + dictValue;
        Object cached = cacheService.get(cacheKey);
        if (cached != null) {
            return cached.toString();
        }

        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictCode, dictCode)
               .eq(SysDict::getDictValue, dictValue)
               .eq(SysDict::getStatus, 1);
        SysDict dict = dictMapper.selectOne(wrapper);

        if (dict != null) {
            cacheService.put(cacheKey, dict.getDictLabel(), CACHE_TTL_MINUTES, TimeUnit.MINUTES);
            return dict.getDictLabel();
        }
        return dictValue;
    }

    public List<SysDict> getDictList(String dictCode) {
        String cacheKey = DICT_CACHE_PREFIX + dictCode;
        Object cached = cacheService.get(cacheKey);
        if (cached != null) {
            @SuppressWarnings("unchecked")
            List<SysDict> list = (List<SysDict>) cached;
            return list;
        }

        LambdaQueryWrapper<SysDict> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysDict::getDictCode, dictCode)
               .eq(SysDict::getStatus, 1)
               .orderByAsc(SysDict::getSort);
        List<SysDict> list = dictMapper.selectList(wrapper);

        if (!list.isEmpty()) {
            cacheService.put(cacheKey, list, CACHE_TTL_MINUTES, TimeUnit.MINUTES);
        }
        return list;
    }

    public List<String> getDictValues(String dictCode) {
        return getDictList(dictCode).stream()
                .map(SysDict::getDictValue)
                .collect(Collectors.toList());
    }

    public void refreshCache(String dictCode) {
        String cacheKey = DICT_CACHE_PREFIX + dictCode;
        cacheService.delete(cacheKey);
        getDictList(dictCode);
    }

    public void clearAllCache() {
        cacheService.clear();
    }
}