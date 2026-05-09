package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysDictType;

import java.util.List;

/**
 * 字典类型 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysDictTypeService extends IService<SysDictType> {

    /**
     * 查询字典类型列表
     *
     * @param dictType 字典类型查询条件
     * @return 字典类型列表
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);

    /**
     * 根据 ID 查询字典类型
     *
     * @param id 字典类型 ID
     * @return 字典类型信息
     */
    SysDictType selectDictTypeById(Long id);

    /**
     * 根据字典类型标识查询
     *
     * @param dictType 字典类型标识
     * @return 字典类型信息
     */
    SysDictType selectDictTypeByType(String dictType);

    /**
     * 创建字典类型
     *
     * @param dictType 字典类型信息
     * @return 是否成功
     */
    boolean createDictType(SysDictType dictType);

    /**
     * 更新字典类型
     *
     * @param dictType 字典类型信息
     * @return 是否成功
     */
    boolean updateDictType(SysDictType dictType);

    /**
     * 删除字典类型
     *
     * @param ids 字典类型 ID 数组
     * @return 是否成功
     */
    boolean deleteDictTypeByIds(Long[] ids);
}
