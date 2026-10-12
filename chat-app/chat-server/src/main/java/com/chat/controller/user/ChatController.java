package com.chat.controller.user;

import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.ChatMessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 私聊消息
 */
@RestController
@RequestMapping("/user/chat")
@Slf4j
@Api(tags = "私聊消息相关接口")
public class ChatController {

    @Autowired
    private ChatMessageService chatMessageService;

    @GetMapping("/getHistory")
    @ApiOperation("分页查询与某人的历史消息")
    public Result<PageResult> getHistory(String targetName,
                                         @RequestParam(defaultValue = "1") Integer page,
                                         @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询历史消息：对方{}，第{}页，每页{}条", targetName, page, pageSize);
        return Result.success(chatMessageService.getHistory(targetName, page, pageSize));
    }

    @GetMapping("/getUnreadCount")
    @ApiOperation("查询我的未读消息数")
    public Result<Integer> getUnreadCount() {
        return Result.success(chatMessageService.countUnread());
    }
}
