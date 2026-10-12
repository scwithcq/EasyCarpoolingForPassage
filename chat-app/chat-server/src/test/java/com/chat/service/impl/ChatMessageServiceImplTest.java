package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.entity.ChatMessage;
import com.chat.entity.Employee;
import com.chat.exception.MessageBusinessException;
import com.chat.mapper.ChatMessageMapper;
import com.chat.result.PageResult;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 聊天消息服务单元测试：拉历史标记已读、统计未读、入参校验
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ChatMessageServiceImplTest {

    @Mock
    private ChatMessageMapper chatMessageMapper;

    @Mock
    private CurrentUserHelper currentUserHelper;

    @InjectMocks
    private ChatMessageServiceImpl chatMessageService;

    private static final String ME = "zhangsan";
    private static final String TARGET = "lisi";

    @AfterEach
    void tearDown() {
        PageHelper.clearPage();
    }

    private void mockLogin() {
        Employee employee = new Employee();
        employee.setUsername(ME);
        when(currentUserHelper.getCurrentUser()).thenReturn(employee);
    }

    @Test
    void getHistoryShouldMarkReadAndReturnPagedResult() {
        mockLogin();
        Page<ChatMessage> page = new Page<>(1, 10);
        page.addAll(Collections.singletonList(new ChatMessage()));
        page.setTotal(1);
        when(chatMessageMapper.getHistory(ME, TARGET)).thenReturn(page);

        PageResult result = chatMessageService.getHistory(TARGET, 1, 10);

        verify(chatMessageMapper).markRead(TARGET, ME);
        assertEquals(1, result.getTotal());
        assertEquals(1, result.getRecords().size());
    }

    @Test
    void getHistoryShouldRejectBlankTarget() {
        assertThrows(MessageBusinessException.class, () -> chatMessageService.getHistory("  ", 1, 10));
    }

    @Test
    void countUnreadShouldReturnMapperCount() {
        mockLogin();
        when(chatMessageMapper.countUnread(anyString())).thenReturn(3);

        assertEquals(3, chatMessageService.countUnread());
    }
}