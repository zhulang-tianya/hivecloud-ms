package com.hivecloud.plugin.engine.spi;

public interface Plugin {

    String name();

    String version();

    default void init() {
    }

    default void start() {
    }

    default void stop() {
    }

    default void destroy() {
    }

    default int order() {
        return 0;
    }
}
