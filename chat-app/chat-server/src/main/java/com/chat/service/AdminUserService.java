package com.chat.service;

import com.chat.dto.EmployeePageQueryDTO;
import com.chat.result.PageResult;

public interface AdminUserService {

    //分页查询用户
    PageResult pageQuery(EmployeePageQueryDTO employeePageQueryDTO);

    //启用/封禁用户
    void updateStatus(Long id, Integer status);

    //删除用户
    void deleteById(Long id);
}