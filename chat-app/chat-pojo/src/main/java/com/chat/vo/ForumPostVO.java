package com.chat.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ForumPostVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String postId;

    private String title;

    private String content;

    private Long categoryId;

    private String categoryName;

    private String stuNumber;

    private String username;

    private String userImage;

    private Integer viewCount;

    private Integer likeCount;

    private Integer commentCount;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}