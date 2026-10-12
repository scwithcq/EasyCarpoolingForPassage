package com.chat.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 聊天消息
 */
@Data
public class ChatMessage implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    /** 发送人用户名 */
    private String fromName;

    /** 接收人用户名 */
    private String toName;

    private String content;

    /** 是否已读：1是 0否 */
    private Integer isRead;

    /** 全局 Jackson 默认只输出到分钟，这里显式补上秒，保证时间格式统一 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
