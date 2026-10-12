package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class ForumPostDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String title;

    private String content;

    private Long categoryId;

    private String stuNumber;

    private String username;

    private String userImage;
}