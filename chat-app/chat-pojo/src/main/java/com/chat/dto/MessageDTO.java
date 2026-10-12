package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class MessageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String content;

    private String parentId;

    private String replyUsername;

    private String stuNumber;

    private String username;

    private String userImage;
}