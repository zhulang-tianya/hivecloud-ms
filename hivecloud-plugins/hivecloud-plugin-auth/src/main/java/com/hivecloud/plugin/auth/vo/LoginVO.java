package com.hivecloud.plugin.auth.vo;

import lombok.Data;

import java.io.Serializable;

@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String username;

    private String password;
}
