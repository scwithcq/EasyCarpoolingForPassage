package com.chat.dto;

import lombok.Data;

@Data
public class WsResultMessageDTO {
    private boolean isSystemMessage;

    private String fromName;

    private Object message;


}
