package com.chat.service.impl;

import com.chat.constant.MessageConstant;
import com.chat.dto.CarpoolPageQueryDTO;
import com.chat.entity.CarpoolOrder;
import com.chat.exception.OrderBusinessException;
import com.chat.mapper.CarpoolOrderMapper;
import com.chat.result.PageResult;
import com.chat.service.AdminCarpoolService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AdminCarpoolServiceImpl implements AdminCarpoolService {

    @Autowired
    private CarpoolOrderMapper carpoolOrderMapper;

    @Override
    public PageResult pageQuery(CarpoolPageQueryDTO carpoolPageQueryDTO) {
        PageHelper.startPage(carpoolPageQueryDTO.getPage(), carpoolPageQueryDTO.getPageSize());
        Page<CarpoolOrder> page = carpoolOrderMapper.pageQuery(carpoolPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        CarpoolOrder carpoolOrder = carpoolOrderMapper.getById(id);
        if (carpoolOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        carpoolOrderMapper.updateStatus(id, status);
    }

    @Override
    public void deleteById(Long id) {
        CarpoolOrder carpoolOrder = carpoolOrderMapper.getById(id);
        if (carpoolOrder == null) {
            throw new OrderBusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        carpoolOrderMapper.deleteById(id);
    }
}