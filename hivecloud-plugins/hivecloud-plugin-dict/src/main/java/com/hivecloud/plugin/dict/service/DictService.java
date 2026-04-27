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

/**
 * 字典服务类
 * 提供字典数据查询和缓存功能
 * 使用二级缓存提升查询性能
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see SysDictMapper
 * @see CacheService
 */
@Slf4j
@RequiredArgsConstructor
public class DictService {

    /**
     * 字典缓存 Key 前缀
     */
    private static final String DICT_CACHE_PREFIX = "hivecloud:dict:";

    /**
     * 缓存过期时间（分钟）
     */
    private static final long CACHE_TTL_MINUTES = 30;

    /**
     * 字典 Mapper 接口
     */
    private final SysDictMapper dictMapper;

    /**
     * 缓存服务接口
     */
    private final CacheService cacheService;

    /**
     * 获取字典标签
     * 先查缓存，未命中则查数据库并回写缓存
     *
     * @param dictCode 字典编码
     * @param dictValue 字典值
     * @return 字典标签，不存在时返回 null
     */
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