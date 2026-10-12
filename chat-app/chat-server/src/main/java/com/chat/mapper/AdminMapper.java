package com.chat.mapper;

import com.chat.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AdminMapper {

    //根据用户名查询管理员
    @Select("select * from chat_app.admin where username = #{username}")
    Admin getByUsername(String username);
}