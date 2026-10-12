package com.chat.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CarpoolOrderDTO {

    private Long id;

    private String departure;

    private String destination;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime departureTime;

    private Integer numberOfPeople;

    private String remark;

    private String stuNumber;

    private String username;

    private String phone;
}
