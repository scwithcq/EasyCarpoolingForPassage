package com.chat.service.impl;

import com.chat.constant.MessageConstant;
import com.chat.constant.StatusConstant;
import com.chat.context.BaseContext;
import com.chat.dto.AnnouncementDTO;
import com.chat.dto.AnnouncementPageQueryDTO;
import com.chat.entity.Announcement;
import com.chat.exception.AnnouncementBusinessException;
import com.chat.mapper.AnnouncementMapper;
import com.chat.result.PageResult;
import com.chat.service.AdminAnnouncementService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Slf4j
public class AdminAnnouncementServiceImpl implements AdminAnnouncementService {

    @Autowired
    private AnnouncementMapper announcementMapper;

    @Override
    public PageResult pageQuery(AnnouncementPageQueryDTO announcementPageQueryDTO) {
        PageHelper.startPage(announcementPageQueryDTO.getPage(), announcementPageQueryDTO.getPageSize());
        Page<Announcement> page = announcementMapper.pageQuery(announcementPageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(AnnouncementDTO announcementDTO) {
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementDTO, announcement);

        if (announcement.getIsTop() == null) {
            announcement.setIsTop(StatusConstant.DISABLE);
        }
        if (announcement.getStatus() == null) {
            announcement.setStatus(StatusConstant.ENABLE);
        }
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        // 创建人/更新人取自 jwt 解析出的当前管理员id
        announcement.setCreateUser(BaseContext.getCurrentId());
        announcement.setUpdateUser(BaseContext.getCurrentId());

        announcementMapper.insert(announcement);
    }

    @Override
    public void update(AnnouncementDTO announcementDTO) {
        if (announcementDTO.getId() == null || announcementMapper.getById(announcementDTO.getId()) == null) {
            throw new AnnouncementBusinessException(MessageConstant.ANNOUNCEMENT_NOT_FOUND);
        }
        Announcement announcement = new Announcement();
        BeanUtils.copyProperties(announcementDTO, announcement);
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setUpdateUser(BaseContext.getCurrentId());
        announcementMapper.update(announcement);
    }

    @Override
    public void deleteById(Long id) {
        if (announcementMapper.getById(id) == null) {
            throw new AnnouncementBusinessException(MessageConstant.ANNOUNCEMENT_NOT_FOUND);
        }
        announcementMapper.deleteById(id);
    }

    @Override
    public void updateStatus(Long id, Integer status) {
        if (announcementMapper.getById(id) == null) {
            throw new AnnouncementBusinessException(MessageConstant.ANNOUNCEMENT_NOT_FOUND);
        }
        announcementMapper.updateStatus(id, status);
    }
}