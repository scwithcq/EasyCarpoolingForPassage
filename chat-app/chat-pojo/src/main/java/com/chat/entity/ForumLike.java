package com.chat.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ForumLike implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String stuNumber;

    /**
     * 点赞对象类型：1-帖子 2-评论
     */
    private Integer targetType;

    private String targetId;

    private LocalDateTime createTime;
}