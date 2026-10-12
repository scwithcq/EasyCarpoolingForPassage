package com.chat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class MessageBoardVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String messageId;

    private String content;

    private String stuNumber;

    private String username;

    private String userImage;

    private String parentId;

    private String replyUsername;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}