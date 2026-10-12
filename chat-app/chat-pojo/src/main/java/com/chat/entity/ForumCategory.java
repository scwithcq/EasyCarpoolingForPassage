package com.chat.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class ForumCategory implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String name;

    private Integer sort;

    private Integer status;
}