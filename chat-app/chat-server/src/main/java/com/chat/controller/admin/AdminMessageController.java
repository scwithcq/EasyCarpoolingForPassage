package com.chat.controller.admin;

import com.chat.dto.MessagePageQueryDTO;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.AdminMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端 - 留言管理
 */
@RestController
@RequestMapping("/admin/message")
@Slf4j
@Api(tags = "管理端-留言管理接口")
public class AdminMessageController {

    @Autowired
    private AdminMessageService adminMessageService;

    @GetMapping("/page")
    @ApiOperation("分页查询留言")
    public Result<PageResult> page(MessagePageQueryDTO messagePageQueryDTO) {
        log.info("管理端分页查询留言：{}", messagePageQueryDTO);
        return Result.success(adminMessageService.pageQuery(messagePageQueryDTO));
    }

    @DeleteMapping("/{messageId}")
    @ApiOperation("删除留言")
    public Result delete(@PathVariable String messageId) {
        log.info("管理端删除留言：{}", messageId);
        adminMessageService.deleteById(messageId);
        return Result.success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改留言状态")
    public Result updateStatus(@PathVariable Integer status, String messageId) {
        log.info("管理端修改留言{}状态为：{}", messageId, status);
        adminMessageService.updateStatus(messageId, status);
        return Result.success();
    }
}