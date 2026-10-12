package com.chat.service;

import com.chat.dto.CarpoolPageQueryDTO;
import com.chat.result.PageResult;

public interface AdminCarpoolService {

    //分页查询拼车订单
    PageResult pageQuery(CarpoolPageQueryDTO carpoolPageQueryDTO);

    //修改拼车订单状态
    void updateStatus(Long id, Integer status);

    //删除拼车订单
    void deleteById(Long id);
}