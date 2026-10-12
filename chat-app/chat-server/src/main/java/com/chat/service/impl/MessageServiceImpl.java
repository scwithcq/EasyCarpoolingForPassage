package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.dto.MessageDTO;
import com.chat.entity.Employee;
import com.chat.entity.MessageBoard;
import com.chat.exception.MessageBusinessException;
import com.chat.mapper.MessageBoardMapper;
import com.chat.result.PageResult;
import com.chat.service.MessageService;
import com.chat.vo.MessageBoardVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageBoardMapper messageBoardMapper;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    @Override
    public void addMessage(MessageDTO messageDTO) {
        if (!StringUtils.hasText(messageDTO.getContent())) {
            throw new MessageBusinessException(MessageConstant.PARAM_ERROR);
        }

        Employee currentUser = currentUserHelper.getCurrentUser();

        MessageBoard messageBoard = new MessageBoard();
        BeanUtils.copyProperties(messageDTO, messageBoard);
        messageBoard.setMessageId("m" + UUID.randomUUID());
        // 作者信息一律取自登录态，不信任前端传参
        messageBoard.setStuNumber(currentUser.getStuNumber());
        messageBoard.setUsername(currentUser.getUsername());
        messageBoard.setUserImage(currentUser.getUserImage());
        messageBoard.setStatus(StatusConstant.ENABLE);
        messageBoard.setCreateTime(LocalDateTime.now());
        messageBoardMapper.insertMessage(messageBoard);
    }

    @Override
    public PageResult getMessageList(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<MessageBoardVO> pageResult = messageBoardMapper.getMessageList();
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public void deleteMessage(String messageId) {
        MessageBoard messageBoard = messageBoardMapper.getByMessageId(messageId);
        if (messageBoard == null) {
            throw new MessageBusinessException(MessageConstant.MESSAGE_NOT_FOUND);
        }
        Employee currentUser = currentUserHelper.getCurrentUser();
        if (!currentUser.getStuNumber().equals(messageBoard.getStuNumber())) {
            throw new MessageBusinessException(MessageConstant.NO_PERMISSION);
        }
        messageBoardMapper.updateStatus(messageId, StatusConstant.DISABLE);
    }
}