package com.chat.controller.admin;

import com.chat.dto.AnnouncementDTO;
import com.chat.dto.AnnouncementPageQueryDTO;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.AdminAnnouncementService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 公告管理
 */
@RestController
@RequestMapping("/admin/announcement")
@Slf4j
@Api(tags = "管理端-公告管理接口")
public class AdminAnnouncementController {

    @Autowired
    private AdminAnnouncementService adminAnnouncementService;

    @GetMapping("/page")
    @ApiOperation("分页查询公告")
    public Result<PageResult> page(AnnouncementPageQueryDTO announcementPageQueryDTO) {
        log.info("管理端分页查询公告：{}", announcementPageQueryDTO);
        return Result.success(adminAnnouncementService.pageQuery(announcementPageQueryDTO));
    }

    @PostMapping
    @ApiOperation("新增公告")
    public Result add(@RequestBody AnnouncementDTO announcementDTO) {
        log.info("管理端新增公告：{}", announcementDTO.getTitle());
        adminAnnouncementService.add(announcementDTO);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("修改公告")
    public Result update(@RequestBody AnnouncementDTO announcementDTO) {
        log.info("管理端修改公告：{}", announcementDTO.getId());
        adminAnnouncementService.update(announcementDTO);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    @ApiOperation("删除公告")
    public Result delete(@PathVariable Long id) {
        log.info("管理端删除公告：{}", id);
        adminAnnouncementService.deleteById(id);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("发布或下线公告")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("管理端修改公告{}状态为：{}", id, status);
        adminAnnouncementService.updateStatus(id, status);
        return Result.success();
    }
}