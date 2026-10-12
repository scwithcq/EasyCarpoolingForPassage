package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端公告分页查询条件
 */
@Data
public class AnnouncementPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int page;

    private int pageSize;

    private String title;

    /**
     * 状态：1已发布 0已下线
     */
    private Integer status;
}