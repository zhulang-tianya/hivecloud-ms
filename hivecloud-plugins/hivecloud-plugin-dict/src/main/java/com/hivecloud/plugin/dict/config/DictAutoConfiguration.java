package com.hivecloud.plugin.dict.config;

import com.hivecloud.plugin.cache.core.CacheService;
import com.hivecloud.plugin.dict.mapper.SysDictMapper;
import com.hivecloud.plugin.dict.service.DictService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;

@AutoConfiguration
@ConditionalOnBean({SysDictMapper.class, CacheService.class})
public class DictAutoConfiguration {

    @Bean
    public DictService dictService(SysDictMapper dictMapper, CacheService cacheService) {
        return new DictService(dictMapper, cacheService);
    }
}