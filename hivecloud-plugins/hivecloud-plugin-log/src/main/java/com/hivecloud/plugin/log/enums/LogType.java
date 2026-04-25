package com.hivecloud.plugin.log.enums;

public enum LogType {

    OPERATE(1, "操作日志"),
    EXCEPTION(2, "异常日志"),
    ACCESS(3, "访问日志");

    private final Integer code;
    private final String description;

    LogType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}