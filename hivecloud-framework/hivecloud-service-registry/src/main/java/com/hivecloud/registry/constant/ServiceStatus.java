package com.hivecloud.registry.constant;

public enum ServiceStatus {

    UP("UP"),
    DOWN("DOWN"),
    OUT_OF_SERVICE("OUT_OF_SERVICE"),
    UNKNOWN("UNKNOWN"),
    REMOVED("REMOVED");

    private final String value;

    ServiceStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }
}
