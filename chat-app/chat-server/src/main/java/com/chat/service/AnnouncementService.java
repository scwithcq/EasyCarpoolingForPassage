package com.chat.service;

import com.chat.result.PageResult;
import com.chat.vo.AnnouncementVO;

/**
 * 用户端公告查询
 */
public interface AnnouncementService {

    //分页查询已发布公告
    PageResult getList(Integer page, Integer pageSize);

    //查询公告详情
    AnnouncementVO getById(Long id);
}