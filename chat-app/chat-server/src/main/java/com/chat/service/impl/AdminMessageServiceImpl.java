package com.chat.service.impl;

import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.dto.MessagePageQueryDTO;
import com.chat.entity.MessageBoard;
import com.chat.exception.MessageBusinessException;
import com.chat.mapper.MessageBoardMapper;
import com.chat.result.PageResult;
import com.chat.service.AdminMessageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminMessageServiceImpl implements AdminMessageService {

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Override
    public PageResult pageQuery(MessagePageQueryDTO messagePageQueryDTO) {
        PageHelper.startPage(messagePageQueryDTO.getPage(), messagePageQueryDTO.getPageSize());
        Page<MessageBoard> page = messageBoardMapper.pageQuery(messagePageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void deleteById(String messageId) {
        MessageBoard messageBoard = messageBoardMapper.getByMessageId(messageId);
        if (messageBoard == null) {
            throw new MessageBusinessException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        messageBoardMapper.updateStatus(messageId, StatusConstant.DISABLE);
    }

    @Override
    public void updateStatus(String messageId, Integer status) {
        MessageBoard messageBoard = messageBoardMapper.getByMessageId(messageId);
        if (messageBoard == null) {
            throw new MessageBusinessException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        messageBoardMapper.updateStatus(messageId, status);
    }
}