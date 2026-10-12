package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端用户分页查询条件
 */
@Data
public class EmployeePageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int page;

    private int pageSize;

    private String stuNumber;

    private String username;

    private String phone;

    /**
     * 状态：1启用 0封禁
     */
    private Integer status;
}