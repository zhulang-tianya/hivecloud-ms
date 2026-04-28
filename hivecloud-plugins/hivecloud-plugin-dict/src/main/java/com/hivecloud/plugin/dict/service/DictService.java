package com.hivecloud.plugin.dict.service;

import com.hivecloud.plugin.dict.entity.DictData;
import com.hivecloud.plugin.dict.entity.DictType;

import java.util.List;
import java.util.Map;

/**
 * 字典服务接口
 * 提供字典类型、字典数据的查询和缓存功能
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 */
public interface DictService {

    /**
     * 获取字典类型列表
     *
     * @return 字典类型列表
     */
    List<DictType> getDictTypes();

    /**
     * 根据字典编码获取字典数据列表
     *
     * @param dictCode 字典编码
     * @return 字典数据列表
     */
    List<DictData> getDictDataByCode(String dictCode);

    /**
     * 获取所有字典数据（按字典编码分组）
     *
     * @return 所有字典数据
     */
    Map<String, List<DictData>> getAllDictData();

    /**
     * 刷新字典缓存
     */
    void refreshCache();
}