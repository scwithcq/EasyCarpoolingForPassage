package com.chat.controller.admin;

import com.chat.dto.EmployeePageQueryDTO;
import com.chat.result.PageResult;
import com.chat.result.Result;
import com.chat.service.AdminUserService;
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
 * 管理端 - 用户管理
 */
@RestController
@RequestMapping("/admin/user")
@Slf4j
@Api(tags = "管理端-用户管理接口")
public class AdminUserController {

    @Autowired
    private AdminUserService adminUserService;

    @GetMapping("/page")
    @ApiOperation("分页查询用户")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
        log.info("管理端分页查询用户：{}", employeePageQueryDTO);
        PageResult pageResult = adminUserService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启用或封禁用户")
    public Result updateStatus(@PathVariable Integer status, Long id) {
        log.info("修改用户{}的状态为：{}", id, status);
        adminUserService.updateStatus(id, status);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("删除用户")
    public Result delete(Long id) {
        log.info("删除用户：{}", id);
        adminUserService.deleteById(id);
        return Result.success();
    }
}