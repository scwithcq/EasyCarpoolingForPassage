package com.chat.controller.user;

import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.AnnouncementService;
import com.chat.vo.AnnouncementVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户端 - 公告信息
 */
@RestController
@RequestMapping("/user/announcement")
@Slf4j
@Api(tags = "公告信息相关接口")
public class AnnouncementController {

    @Autowired
    private AnnouncementService announcementService;

    @GetMapping("/getList")
    @ApiOperation("分页查询已发布公告")
    public Result<PageResult> getList(@RequestParam(defaultValue = "1") Integer page,
                                      @RequestParam(defaultValue = "10") Integer pageSize) {
        log.info("查询公告列表：第{}页，每页{}条", page, pageSize);
        return Result.success(announcementService.getList(page, pageSize));
    }

    @GetMapping("/getById")
    @ApiOperation("查询公告详情")
    public Result<AnnouncementVO> getById(Long id) {
        log.info("查询公告详情：{}", id);
        return Result.success(announcementService.getById(id));
    }
}