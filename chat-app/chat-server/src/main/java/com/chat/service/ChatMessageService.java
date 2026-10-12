package com.chat.service;

import com.chat.entity.ChatMessage;
import com.chat.result.PageResult;

public interface ChatMessageService {

    //保存聊天消息
    void save(ChatMessage chatMessage);

    //分页查询与某人的历史消息（并把我方未读标记为已读）
    PageResult getHistory(String targetName, Integer page, Integer pageSize);

    //查询我的未读消息数
    int countUnread();
}
