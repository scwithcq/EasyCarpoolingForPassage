package com.chat.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 公告
 */
@Data
public class Announcement implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String title;

    private String content;

    private String coverImage;

    /**
     * 是否置顶：1是 0否
     */
    private Integer isTop;

    /**
     * 状态：1已发布 0已下线
     */
    private Integer status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Long createUser;

    private Long updateUser;
}