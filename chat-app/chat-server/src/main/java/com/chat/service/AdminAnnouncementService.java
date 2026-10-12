package com.chat.service;

import com.chat.dto.AnnouncementDTO;
import com.chat.dto.AnnouncementPageQueryDTO;
import com.chat.result.PageResult;

public interface AdminAnnouncementService {

    //分页查询公告
    PageResult pageQuery(AnnouncementPageQueryDTO announcementPageQueryDTO);

    //新增公告
    void add(AnnouncementDTO announcementDTO);

    //修改公告
    void update(AnnouncementDTO announcementDTO);

    //删除公告
    void deleteById(Long id);

    //修改公告状态（发布/下线）
    void updateStatus(Long id, Integer status);
}