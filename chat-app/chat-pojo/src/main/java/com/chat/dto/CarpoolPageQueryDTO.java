package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端拼车分页查询条件
 */
@Data
public class CarpoolPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int page;

    private int pageSize;

    private String stuNumber;

    private String departure;

    private String destination;

    /**
     * 状态：1有效 0失效
     */
    private Integer status;
}