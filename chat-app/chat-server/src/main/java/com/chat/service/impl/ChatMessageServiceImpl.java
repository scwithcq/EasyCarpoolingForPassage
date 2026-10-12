package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.constant.MessageConstant;
import com.chat.entity.ChatMessage;
import com.chat.entity.Employee;
import com.chat.exception.MessageBusinessException;
import com.chat.mapper.ChatMessageMapper;
import com.chat.result.PageResult;
import com.chat.service.ChatMessageService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@Slf4j
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    @Override
    public void save(ChatMessage chatMessage) {
        chatMessageMapper.insert(chatMessage);
    }

    @Override
    public PageResult getHistory(String targetName, Integer page, Integer pageSize) {
        if (!StringUtils.hasText(targetName)) {
            throw new MessageBusinessException(MessageConstant.PARAM_ERROR);
        }
        Employee currentUser = currentUserHelper.getCurrentUser();
        String me = currentUser.getUsername();

        // 拉取历史即视为已读
        chatMessageMapper.markRead(targetName, me);

        PageHelper.startPage(page, pageSize);
        Page<ChatMessage> messages = chatMessageMapper.getHistory(me, targetName);
        return new PageResult(messages.getTotal(), messages.getResult());
    }

    @Override
    public int countUnread() {
        Employee currentUser = currentUserHelper.getCurrentUser();
        return chatMessageMapper.countUnread(currentUser.getUsername());
    }
}
