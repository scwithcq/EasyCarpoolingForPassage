package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ForumCommentDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String postId;

    private String content;

    private String parentId;

    private String replyUsername;

    private String stuNumber;

    private String username;

    private String userImage;
}