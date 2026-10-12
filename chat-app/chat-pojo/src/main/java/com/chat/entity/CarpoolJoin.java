package com.chat.entity;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 拼车接单记录
 */
@Data
public class CarpoolJoin implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderId;

    private String stuNumber;

    private String username;

    private String phone;

    private LocalDateTime createTime;
}
