package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端帖子分页查询条件
 */
@Data
public class ForumPostPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int page;

    private int pageSize;

    private String title;

    private Long categoryId;

    private String stuNumber;

    /**
     * 状态：1正常 0已删除
     */
    private Integer status;
}