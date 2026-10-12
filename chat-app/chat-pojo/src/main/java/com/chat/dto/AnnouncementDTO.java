package com.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 公告新增/修改参数
 */
@Data
public class AnnouncementDTO implements Serializable {
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
}