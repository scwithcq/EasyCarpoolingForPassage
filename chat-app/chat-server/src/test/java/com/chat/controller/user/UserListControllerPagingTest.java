package com.chat.controller.user;

import com.chat.result.PageResult;
import com.chat.service.CarpoolOrderService;
import com.chat.service.ForumService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 列表接口分页默认值单元测试：不传 page/pageSize 时应使用 1 / 10，避免 NPE
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserListControllerPagingTest {

    @Mock
    private CarpoolOrderService carpoolOrderService;

    @Mock
    private ForumService forumService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        CarpoolOrderController carpoolController = new CarpoolOrderController();
        ReflectionTestUtils.setField(carpoolController, "carpoolOrderService", carpoolOrderService);

        ForumController forumController = new ForumController();
        ReflectionTestUtils.setField(forumController, "forumService", forumService);

        mockMvc = MockMvcBuilders.standaloneSetup(carpoolController, forumController).build();

        when(carpoolOrderService.getCarpool(any(), any())).thenReturn(new PageResult(0, Collections.emptyList()));
        when(carpoolOrderService.getMyCarpool(any(), any())).thenReturn(new PageResult(0, Collections.emptyList()));
        when(carpoolOrderService.getMyJoin(any(), any())).thenReturn(new PageResult(0, Collections.emptyList()));
        when(forumService.getPostList(any(), any(), any())).thenReturn(new PageResult(0, Collections.emptyList()));
        when(forumService.getMyPost(any(), any())).thenReturn(new PageResult(0, Collections.emptyList()));
    }

    @Test
    void carpoolListShouldUseDefaultPaging() throws Exception {
        mockMvc.perform(get("/user/order/getCarpool")).andExpect(status().isOk());
        verify(carpoolOrderService).getCarpool(1, 10);

        mockMvc.perform(get("/user/order/getMyCarpool")).andExpect(status().isOk());
        verify(carpoolOrderService).getMyCarpool(1, 10);

        mockMvc.perform(get("/user/order/getMyJoin")).andExpect(status().isOk());
        verify(carpoolOrderService).getMyJoin(1, 10);
    }

    @Test
    void forumListShouldUseDefaultPaging() throws Exception {
        mockMvc.perform(get("/user/forum/getPostList")).andExpect(status().isOk());
        verify(forumService).getPostList(1, 10, null);

        mockMvc.perform(get("/user/forum/getMyPost")).andExpect(status().isOk());
        verify(forumService).getMyPost(1, 10);
    }
}