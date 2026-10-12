package com.chat.service;

import com.chat.dto.MessagePageQueryDTO;
import com.chat.result.PageResult;

public interface AdminMessageService {

    //分页查询留言
    PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO);

    //删除留言（逻辑删除）
    void deleteById(String messageId);

    //修改留言状态
    void updateStatus(String messageId, Integer status);
}