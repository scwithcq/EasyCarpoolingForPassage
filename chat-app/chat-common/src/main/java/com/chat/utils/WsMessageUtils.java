package com.chat.utils;

import com.alibaba.fastjson.JSON;
import com.chat.dto.WsResultMessageDTO;

public class WsMessageUtils {
    public static String getMessage(boolean isSystemMessage, String fromName, Object message) {
        WsResultMessageDTO result = new WsResultMessageDTO();
        result.setSystemMessage(isSystemMessage);
        result.setMessage(message);
        if (fromName != null) {
            result.setFromName(fromName);
        }
        return JSON.toJSONString(result);
    }
}