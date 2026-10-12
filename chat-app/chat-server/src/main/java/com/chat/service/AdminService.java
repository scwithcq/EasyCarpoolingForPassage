package com.chat.service;

import com.chat.dto.AdminLoginDTO;
import com.chat.entity.Admin;

public interface AdminService {

    //管理员登录
    Admin login(AdminLoginDTO adminLoginDTO);
}