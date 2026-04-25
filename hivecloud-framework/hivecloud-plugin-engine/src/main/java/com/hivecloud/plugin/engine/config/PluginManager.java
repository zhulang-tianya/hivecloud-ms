package com.hivecloud.plugin.engine.config;

import com.hivecloud.plugin.engine.spi.HiveCloudPlugin;
import com.hivecloud.plugin.engine.spi.Plugin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class PluginManager {

    private final Map<String, Plugin> plugins = new ConcurrentHashMap<>();

    @Autowired
    private ApplicationContext applicationContext;

    @EventListener(ApplicationReadyEvent.class)
    public void loadPlugins() {
        Map<String, Plugin> pluginBeans = applicationContext.getBeansOfType(Plugin.class);
        if (pluginBeans.isEmpty()) {
            log.info("No plugins found");
            return;
        }

        List<Plugin> sortedPlugins = pluginBeans.values().stream()
                .sorted(AnnotationAwareOrderComparator.INSTANCE)
                .toList();

        for (Plugin plugin : sortedPlugins) {
            String name = getPluginName(plugin);
            plugins.put(name, plugin);
            log.info("Loading plugin: {} v{}", name, getPluginVersion(plugin));
            try {
                plugin.init();
                plugin.start();
                log.info("Plugin started: {} v{}", name, getPluginVersion(plugin));
            } catch (Exception e) {
                log.error("Failed to start plugin: {}", name, e);
            }
        }
        log.info("Total plugins loaded: {}", plugins.size());
    }

    public Plugin getPlugin(String name) {
        return plugins.get(name);
    }

    public Map<String, Plugin> getAllPlugins() {
        return Map.copyOf(plugins);
    }

    public boolean hasPlugin(String name) {
        return plugins.containsKey(name);
    }

    public void stopAll() {
        plugins.values().forEach(plugin -> {
            try {
                plugin.stop();
                plugin.destroy();
            } catch (Exception e) {
                log.error("Failed to stop plugin: {}", getPluginName(plugin), e);
            }
        });
        plugins.clear();
    }

    private String getPluginName(Plugin plugin) {
        HiveCloudPlugin annotation = plugin.getClass().getAnnotation(HiveCloudPlugin.class);
        return annotation != null ? annotation.name() : plugin.getClass().getSimpleName();
    }

    private String getPluginVersion(Plugin plugin) {
        HiveCloudPlugin annotation = plugin.getClass().getAnnotation(HiveCloudPlugin.class);
        return annotation != null ? annotation.version() : "unknown";
    }
}
