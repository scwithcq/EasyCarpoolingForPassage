package com.chat.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ForumComment implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String commentId;

    private String postId;

    private String content;

    private String stuNumber;

    private String username;

    private String userImage;

    private String parentId;

    private String replyUsername;

    private Integer likeCount;

    private Integer status;

    private LocalDateTime createTime;
}