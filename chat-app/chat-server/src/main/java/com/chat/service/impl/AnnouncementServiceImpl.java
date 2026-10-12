package com.chat.service.impl;

import com.chat.constant.MessageConstant;
import com.chat.exception.AnnouncementBusinessException;
import com.chat.mapper.AnnouncementMapper;
import com.chat.result.PageResult;
import com.chat.service.AnnouncementService;
import com.chat.vo.AnnouncementVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class AnnouncementServiceImpl implements AnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public PageResult getList(Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        Page<AnnouncementVO> pageResult = announcementMapper.getPublishedList();
        return new PageResult(pageResult.getTotal(), pageResult.getResult());
    }

    @Override
    public AnnouncementVO getById(Long id) {
        AnnouncementVO announcementVO = announcementMapper.getPublishedById(id);
        if (announcementVO == null) {
            throw new AnnouncementBusinessException(MessageConstant.ANNOUNCEMENT_NOT_FOUND);
        }
        return announcementVO;
    }
}