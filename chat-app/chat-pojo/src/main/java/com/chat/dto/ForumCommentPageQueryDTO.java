package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 管理端评论分页查询条件
 */
@Data
public class ForumCommentPageQueryDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int page;

    private int pageSize;

    private String postId;

    private String content;

    private String stuNumber;

    /**
     * 状态：1正常 0已删除
     */
    private Integer status;
}