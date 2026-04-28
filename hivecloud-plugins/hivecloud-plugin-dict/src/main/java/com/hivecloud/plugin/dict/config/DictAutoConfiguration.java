package com.hivecloud.plugin.dict.config;

import com.hivecloud.plugin.cache.core.CacheService;
import com.hivecloud.plugin.dict.mapper.DictTypeMapper;
import com.hivecloud.plugin.dict.service.DictService;
import com.hivecloud.plugin.dict.service.impl.DictServiceImpl;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * 字典模块自动配置类
 * 根据条件自动装配字典服务 Bean
 * 依赖 DictTypeMapper 和 CacheService
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see DictService
 * @see DictServiceImpl
 * @see DictTypeMapper
 * @see CacheService
 */
@AutoConfiguration
@ConditionalOnBean({DictTypeMapper.class, CacheService.class})
public class DictAutoConfiguration {

    /**
     * 创建字典服务 Bean
     *
     * @param dictTypeMapper 字典类型 Mapper 接口
     * @param cacheService 缓存服务接口
     * @return 字典服务实例
     */
    @Bean
    public DictService dictService(DictTypeMapper dictTypeMapper, CacheService cacheService) {
        return new DictServiceImpl(dictTypeMapper, cacheService);
    }
}