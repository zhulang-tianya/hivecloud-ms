package com.hivecloud.plugin.dict.config;

import com.hivecloud.plugin.cache.core.CacheService;
import com.hivecloud.plugin.dict.mapper.SysDictMapper;
import com.hivecloud.plugin.dict.service.DictService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

/**
 * 字典模块自动配置类
 * 根据条件自动装配字典服务 Bean
 * 依赖 SysDictMapper 和 CacheService
 *
 * @author HiveCloud Team
 * @date 2026-04-25
 * @see DictService
 * @see SysDictMapper
 * @see CacheService
 */
@AutoConfiguration
@ConditionalOnBean({SysDictMapper.class, CacheService.class})
public class DictAutoConfiguration {

    /**
     * 创建字典服务 Bean
     *
     * @param dictMapper 字典 Mapper 接口
     * @param cacheService 缓存服务接口
     * @return 字典服务实例
     */
    @Bean
    public DictService dictService(SysDictMapper dictMapper, CacheService cacheService) {
        return new DictService(dictMapper, cacheService);
    }
}