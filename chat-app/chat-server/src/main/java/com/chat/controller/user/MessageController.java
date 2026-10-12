package com.chat.controller.user;

import com.chat.dto.MessageDTO;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户端 - 留言反馈
 */
@RestController
@RequestMapping("/user/message")
@Slf4j
@Api(tags = "留言反馈相关接口")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @PostMapping("/addMessage")
    @ApiOperation("发表留言或回复")
    public Result addMessage(@RequestBody MessageDTO messageDTO) {
        log.info("发表留言：{}", messageDTO);
        messageService.addMessage(messageDTO);
        return Result.success();
    }

    @GetMapping("/getMessageList")
    @ApiOperation("分页查询留言墙")
    public Result<PageResult> getMessageList(@RequestParam(defaultValue = "1") Integer page,
                                             @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询留言墙：第{}页，每页{}条", page, pageSize);
        return Result.success(messageService.getMessageList(page, pageSize));
    }

    @DeleteMapping("/deleteMessage")
    @ApiOperation("删除我的留言")
    public Result deleteMessage(String messageId) {
        log.info("删除留言：{}", messageId);
        messageService.deleteMessage(messageId);
        return Result.success();
    }
}