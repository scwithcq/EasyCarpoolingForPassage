package com.chat.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ForumPost implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String postId;

    private String title;

    private String content;

    private Long categoryId;

    private String stuNumber;

    private String username;

    private String userImage;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}