package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.entity.CarpoolJoin;
import com.chat.entity.CarpoolOrder;
import com.chat.entity.Employee;
import com.chat.exception.OrderBusinessException;
import com.chat.mapper.CarpoolJoinMapper;
import com.chat.mapper.CarpoolOrderMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * 拼车接单核心链路单元测试
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class CarpoolOrderServiceImplTest {

    @Mock
    private CarpoolOrderMapper carpoolOrderMapper;

    @Mock
    private CarpoolJoinMapper carpoolJoinMapper;

    @Mock
    private CurrentUserHelper currentUserHelper;

    @InjectMocks
    private CarpoolOrderServiceImpl carpoolOrderService;

    private static final String OWNER_STU = "202204040114";
    private static final String JOINER_STU = "202204040115";

    private CarpoolOrder order;

    @BeforeEach
    void setUp() {
        order = new CarpoolOrder();
        order.setId(1L);
        order.setOrderId("xyt-test");
        order.setStuNumber(OWNER_STU);
        order.setStatus(1);
        order.setNumberOfPeople(2);
        order.setJoinedNumber(0);

        when(carpoolOrderMapper.getById(anyLong())).thenReturn(order);
    }

    private Employee user(String stuNumber) {
        Employee employee = new Employee();
        employee.setStuNumber(stuNumber);
        employee.setUsername("u" + stuNumber);
        employee.setPhone("13800000000");
        return employee;
    }

    @Test
    void joinCarpoolShouldInsertAndIncreaseJoinedNumber() {
        when(currentUserHelper.getCurrentUser()).thenReturn(user(JOINER_STU));
        when(carpoolJoinMapper.getByOrderAndStu(anyString(), anyString())).thenReturn(null);

        carpoolOrderService.joinCarpool(1L);

        verify(carpoolJoinMapper).insert(any(CarpoolJoin.class));
        verify(carpoolOrderMapper).updateJoinedNumber(1L, 1);
    }

    @Test
    void joinCarpoolShouldRejectOwnOrder() {
        when(currentUserHelper.getCurrentUser()).thenReturn(user(OWNER_STU));

        assertThrows(OrderBusinessException.class, () -> carpoolOrderService.joinCarpool(1L));

        verify(carpoolJoinMapper, never()).insert(any(CarpoolJoin.class));
        verify(carpoolOrderMapper, never()).updateJoinedNumber(anyLong(), anyInt());
    }

    @Test
    void joinCarpoolShouldRejectDuplicateJoin() {
        when(currentUserHelper.getCurrentUser()).thenReturn(user(JOINER_STU));
        when(carpoolJoinMapper.getByOrderAndStu(anyString(), anyString())).thenReturn(new CarpoolJoin());

        assertThrows(OrderBusinessException.class, () -> carpoolOrderService.joinCarpool(1L));

        verify(carpoolJoinMapper, never()).insert(any(CarpoolJoin.class));
    }

    @Test
    void joinCarpoolShouldRejectWhenOrderFull() {
        order.setJoinedNumber(2);
        when(currentUserHelper.getCurrentUser()).thenReturn(user(JOINER_STU));
        when(carpoolJoinMapper.getByOrderAndStu(anyString(), anyString())).thenReturn(null);

        assertThrows(OrderBusinessException.class, () -> carpoolOrderService.joinCarpool(1L));

        verify(carpoolJoinMapper, never()).insert(any(CarpoolJoin.class));
        verify(carpoolOrderMapper, never()).updateJoinedNumber(anyLong(), anyInt());
    }

    @Test
    void deleteCarpoolShouldRejectOtherUser() {
        when(currentUserHelper.getCurrentUser()).thenReturn(user(JOINER_STU));

        assertThrows(OrderBusinessException.class, () -> carpoolOrderService.deleteCarpool(1L));

        verify(carpoolOrderMapper, never()).deleteById(anyLong());
    }

    @Test
    void deleteCarpoolShouldPassForOwner() {
        when(currentUserHelper.getCurrentUser()).thenReturn(user(OWNER_STU));

        carpoolOrderService.deleteCarpool(1L);

        verify(carpoolOrderMapper).deleteById(1L);
    }
}