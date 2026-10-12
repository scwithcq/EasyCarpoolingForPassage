package com.chat.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarpoolOrder {
    private static final long serialVersionUID = 1L;

    private Long id;

    private String orderId;

    private String departure;

    private String destination;

    private LocalDateTime departureTime;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer numberOfPeople;

    private Integer joinedNumber;

    private String remark;

    private Integer status;

    private String stuNumber;

    private String username;

    private String phone;
}
