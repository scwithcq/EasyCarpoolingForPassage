package com.chat.service.impl;

import com.chat.component.CurrentUserHelper;
import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.dto.CarpoolOrderDTO;
import com.chat.entity.CarpoolJoin;
import com.chat.entity.CarpoolOrder;
import com.chat.entity.Employee;
import com.chat.exception.OrderBusinessException;
import com.chat.mapper.CarpoolJoinMapper;
import com.chat.mapper.CarpoolOrderMapper;
import com.chat.result.PageResult;
import com.chat.service.CarpoolOrderService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class CarpoolOrderServiceImpl implements CarpoolOrderService {

    @Autowired
    private CarpoolOrderMapper carpoolOrderMapper;

    @Autowired
    private CarpoolJoinMapper carpoolJoinMapper;

    @Autowired
    private CurrentUserHelper currentUserHelper;

    //发布拼车
    @Override
    public void addCarpool(CarpoolOrderDTO carpoolOrderDTO) {
        if (!StringUtils.hasText(carpoolOrderDTO.getDeparture())
                || !StringUtils.hasText(carpoolOrderDTO.getDestination())
                || carpoolOrderDTO.getDepartureTime() == null) {
            throw new OrderBusinessException(MessageConstant.PARAM_ERROR);
        }

        Employee currentUser = currentUserHelper.getCurrentUser();

        CarpoolOrder carpoolOrder = new CarpoolOrder();
        BeanUtils.copyProperties(carpoolOrderDTO, carpoolOrder);
        carpoolOrder.setOrderId("xyt" + UUID.randomUUID());
        // 发布人信息一律取自登录态，不信任前端传参
        carpoolOrder.setStuNumber(currentUser.getStuNumber());
        carpoolOrder.setUsername(currentUser.getUsername());
        carpoolOrder.setPhone(currentUser.getPhone());
        carpoolOrder.setCreateTime(LocalDateTime.now());
        carpoolOrder.setUpdateTime(LocalDateTime.now());
        carpoolOrder.setStatus(StatusConstant.ENABLE);

        carpoolOrderMapper.addCarpool(carpoolOrder);
    }

    //拼车订单查询
    @Override
    public PageResult getCarpool(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<CarpoolOrder> carpoolOrders = carpoolOrderMapper.getCarpool();
        return new PageResult(carpoolOrders.getTotal(), carpoolOrders.getResult());
    }

    @Override
    public CarpoolOrder getCarpoolById(Long id) {
        CarpoolOrder carpoolOrder = carpoolOrderMapper.getById(id);
        if (carpoolOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        return carpoolOrder;
    }

    @Override
    public PageResult getMyCarpool(Integer page, Integer pageSize) {
        Employee currentUser = currentUserHelper.getCurrentUser();
        PageHelper.startPage(page, pageSize);
        Page<CarpoolOrder> carpoolOrders = carpoolOrderMapper.getMyCarpool(currentUser.getStuNumber());
        return new PageResult(carpoolOrders.getTotal(), carpoolOrders.getResult());
    }

    @Override
    public void updateCarpool(CarpoolOrderDTO carpoolOrderDTO) {
        if (carpoolOrderDTO.getId() == null) {
            throw new OrderBusinessException(MessageConstant.PARAM_ERROR);
        }
        CarpoolOrder carpoolOrder = carpoolOrderMapper.getById(carpoolOrderDTO.getId());
        if (carpoolOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 仅本人可修改
        Employee currentUser = currentUserHelper.getCurrentUser();
        if (!currentUser.getStuNumber().equals(carpoolOrder.getStuNumber())) {
            throw new OrderBusinessException(MessageConstant.NO_PERMISSION);
        }

        CarpoolOrder update = new CarpoolOrder();
        BeanUtils.copyProperties(carpoolOrderDTO, update);
        update.setUpdateTime(LocalDateTime.now());
        carpoolOrderMapper.updateCarpool(update);
    }

    @Override
    public void deleteCarpool(Long id) {
        CarpoolOrder carpoolOrder = carpoolOrderMapper.getById(id);
        if (carpoolOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 仅本人可删除
        Employee currentUser = currentUserHelper.getCurrentUser();
        if (!currentUser.getStuNumber().equals(carpoolOrder.getStuNumber())) {
            throw new OrderBusinessException(MessageConstant.NO_PERMISSION);
        }
        carpoolOrderMapper.deleteById(id);
    }

    //接单
    @Override
    public void joinCarpool(Long id) {
        CarpoolOrder carpoolOrder = getCarpoolById(id);
        if (!StatusConstant.ENABLE.equals(carpoolOrder.getStatus())) {
            throw new OrderBusinessException(MessageConstant.ORDER_STATUS_ERROR);
        }
        Employee currentUser = currentUserHelper.getCurrentUser();
        // 不能接自己发布的拼车
        if (currentUser.getStuNumber().equals(carpoolOrder.getStuNumber())) {
            throw new OrderBusinessException(MessageConstant.CANNOT_JOIN_OWN);
        }
        // 不能重复接单
        if (carpoolJoinMapper.getByOrderAndStu(carpoolOrder.getOrderId(), currentUser.getStuNumber()) != null) {
            throw new OrderBusinessException(MessageConstant.ALREADY_JOINED);
        }
        int joined = carpoolOrder.getJoinedNumber() == null ? 0 : carpoolOrder.getJoinedNumber();
        // 人数已满
        if (carpoolOrder.getNumberOfPeople() != null && joined >= carpoolOrder.getNumberOfPeople()) {
            throw new OrderBusinessException(MessageConstant.ORDER_FULL);
        }

        CarpoolJoin carpoolJoin = new CarpoolJoin();
        carpoolJoin.setOrderId(carpoolOrder.getOrderId());
        carpoolJoin.setStuNumber(currentUser.getStuNumber());
        carpoolJoin.setUsername(currentUser.getUsername());
        carpoolJoin.setPhone(currentUser.getPhone());
        carpoolJoin.setCreateTime(LocalDateTime.now());
        carpoolJoinMapper.insert(carpoolJoin);

        carpoolOrderMapper.updateJoinedNumber(id, joined + 1);
    }

    //取消接单
    @Override
    public void cancelJoin(Long id) {
        CarpoolOrder carpoolOrder = getCarpoolById(id);
        Employee currentUser = currentUserHelper.getCurrentUser();
        if (carpoolJoinMapper.getByOrderAndStu(carpoolOrder.getOrderId(), currentUser.getStuNumber()) == null) {
            throw new OrderBusinessException(MessageConstant.NOT_JOINED);
        }
        carpoolJoinMapper.deleteByOrderAndStu(carpoolOrder.getOrderId(), currentUser.getStuNumber());
        int joined = carpoolOrder.getJoinedNumber() == null ? 0 : carpoolOrder.getJoinedNumber();
        carpoolOrderMapper.updateJoinedNumber(id, Math.max(0, joined - 1));
    }

    @Override
    public List<CarpoolJoin> getJoinList(Long id) {
        CarpoolOrder carpoolOrder = getCarpoolById(id);
        return carpoolJoinMapper.getJoinList(carpoolOrder.getOrderId());
    }

    @Override
    public PageResult getMyJoin(Integer page, Integer pageSize) {
        Employee currentUser = currentUserHelper.getCurrentUser();
        PageHelper.startPage(page, pageSize);
        Page<CarpoolOrder> carpoolOrders = carpoolJoinMapper.getMyJoin(currentUser.getStuNumber());
        return new PageResult(carpoolOrders.getTotal(), carpoolOrders.getResult());
    }
}