package com.hivecloud.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hivecloud.system.entity.SysDictData;

import java.util.List;

/**
 * 字典数据 Service 接口
 *
 * @author HiveCloud Team
 * @date 2026-05-09
 */
public interface SysDictDataService extends IService<SysDictData> {

    /**
     * 查询字典数据列表
     *
     * @param dictData 字典数据查询条件
     * @return 字典数据列表
     */
    List<SysDictData> selectDictDataList(SysDictData dictData);

    /**
     * 根据 ID 查询字典数据
     *
     * @param id 字典数据 ID
     * @return 字典数据信息
     */
    SysDictData selectDictDataById(Long id);

    /**
     * 根据字典类型查询数据
     *
     * @param dictTypeId 字典类型 ID
     * @return 字典数据列表
     */
    List<SysDictData> selectDictDataByTypeId(Long dictTypeId);

    /**
     * 创建字典数据
     *
     * @param dictData 字典数据信息
     * @return 是否成功
     */
    boolean createDictData(SysDictData dictData);

    /**
     * 更新字典数据
     *
     * @param dictData 字典数据信息
     * @return 是否成功
     */
    boolean updateDictData(SysDictData dictData);

    /**
     * 删除字典数据
     *
     * @param ids 字典数据 ID 数组
     * @return 是否成功
     */
    boolean deleteDictDataByIds(Long[] ids);
}
