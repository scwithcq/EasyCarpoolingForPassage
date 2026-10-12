package com.chat.controller.admin;

import com.chat.dto.CarpoolPageQueryDTO;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.AdminCarpoolService;
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
 * 管理端 - 拼车管理
 */
@RestController
@RequestMapping("/admin/carpool")
@Slf4j
@Api(tags = "管理端-拼车管理接口")
public class AdminCarpoolController {

    @Autowired
    private AdminCarpoolService adminCarpoolService;

    @GetMapping("/page")
    @ApiOperation("分页查询拼车订单")
    public Result<PageResult> page(CarpoolPageQueryDTO carpoolPageQueryDTO) {
        log.info("管理端分页查询拼车订单：{}", carpoolPageQueryDTO);
        PageResult pageResult = adminCarpoolService.pageQuery(carpoolPageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("修改拼车订单状态")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("修改拼车订单{}的状态为：{}", id, status);
        adminCarpoolService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除拼车订单")
    public Result delete(Long id) {
        log.info("删除拼车订单：{}", id);
        adminCarpoolService.deleteById(id);
        return Result.success();
    }
}