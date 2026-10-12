package com.chat.service;

import com.chat.dto.MessageDTO;
import com.chat.result.PageResult;

public interface MessageService {

    //发表留言或回复
    void addMessage(MessageDTO messageDTO);

    //分页查询留言墙
    PageResult getMessageList(Integer page, Integer pageSize);

    //删除自己的留言
    void deleteMessage(String messageId);
}